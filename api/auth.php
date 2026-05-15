<?php
session_start();
require_once 'db.php';

header('Content-Type: application/json');

$action = isset($_GET['action']) ? $_GET['action'] : '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    if (!is_array($data)) {
        $data = [];
    }
    
    if ($action === 'register') {
        $name = trim($data['name'] ?? '');
        $email = trim($data['email'] ?? '');
        $rawPassword = $data['password'] ?? '';
        $role = $data['role'] ?? 'fresher';
        $allowedRoles = ['fresher', 'investor', 'mentor'];
        if ($name === '' || $email === '' || strlen($rawPassword) < 6 || !in_array($role, $allowedRoles, true)) {
            echo json_encode(["status" => "error", "message" => "Invalid registration data."]);
            exit;
        }
        $password = password_hash($rawPassword, PASSWORD_DEFAULT);

        try {
            $stmt = $pdo->prepare("INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)");
            $stmt->execute([$name, $email, $password, $role]);
            
            $userId = $pdo->lastInsertId();
            
            $_SESSION['user_id'] = $userId;
            $_SESSION['role'] = $role;
            $_SESSION['name'] = $name;

            echo json_encode(["status" => "success", "user" => ["id" => $userId, "name" => $name, "role" => $role]]);
        } catch(PDOException $e) {
            echo json_encode(["status" => "error", "message" => "Email already exists or error occurred."]);
        }
    } 
    elseif ($action === 'login') {
        $email = $data['email'] ?? '';
        $password = $data['password'] ?? '';

        $stmt = $pdo->prepare("SELECT * FROM users WHERE email = ?");
        $stmt->execute([$email]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($user && password_verify($password, $user['password'])) {
            $_SESSION['user_id'] = $user['id'];
            $_SESSION['role'] = $user['role'];
            $_SESSION['name'] = $user['name'];
            echo json_encode(["status" => "success", "user" => ["id" => $user['id'], "name" => $user['name'], "role" => $user['role']]]);
        } else {
            echo json_encode(["status" => "error", "message" => "Invalid email or password."]);
        }
    }
} elseif ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if ($action === 'logout') {
        session_destroy();
        echo json_encode(["status" => "success"]);
    } elseif ($action === 'me') {
        if (isset($_SESSION['user_id'])) {
            $stmt = $pdo->prepare("SELECT photo FROM users WHERE id = ?");
            $stmt->execute([$_SESSION['user_id']]);
            $userRow = $stmt->fetch(PDO::FETCH_ASSOC);
            echo json_encode(["status" => "success", "user" => ["id" => $_SESSION['user_id'], "name" => $_SESSION['name'], "role" => $_SESSION['role'], "photo" => $userRow['photo']]]);
        } else {
            echo json_encode(["status" => "error", "message" => "Not logged in."]);
        }
    }
}
?>
