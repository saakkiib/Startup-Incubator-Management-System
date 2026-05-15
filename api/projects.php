<?php
session_start();
require_once 'db.php';

header('Content-Type: application/json');

$action = isset($_GET['action']) ? $_GET['action'] : '';

function jsonError($message) {
    echo json_encode(["status" => "error", "message" => $message]);
    exit;
}

if (!isset($_SESSION['user_id'])) {
    jsonError("Unauthorized");
}

$user_id = $_SESSION['user_id'];
$role = $_SESSION['role'];

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if ($action === 'list') {
        if ($role === 'admin') {
            // Admin sees all projects
            $stmt = $pdo->query("SELECT p.*, u.name as author_name FROM projects p JOIN users u ON p.author_id = u.id ORDER BY p.created_at DESC");
        } elseif ($role === 'fresher') {
            // Fresher sees only their own
            $stmt = $pdo->prepare("SELECT p.*, u.name as author_name FROM projects p JOIN users u ON p.author_id = u.id WHERE p.author_id = ? ORDER BY p.created_at DESC");
            $stmt->execute([$user_id]);
        } else {
            // Investors and Mentors see approved projects
            $stmt = $pdo->query("SELECT p.*, u.name as author_name FROM projects p JOIN users u ON p.author_id = u.id WHERE p.status = 'approved' ORDER BY p.created_at DESC");
        }
        $projects = $stmt->fetchAll(PDO::FETCH_ASSOC);
        echo json_encode(["status" => "success", "data" => $projects]);
    } else {
        jsonError("Invalid action.");
    }
} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    if (!is_array($data)) {
        $data = [];
    }
    
    if ($action === 'add' && $role === 'fresher') {
        $title = trim($data['title'] ?? '');
        $description = trim($data['description'] ?? '');
        $funding_goal = isset($data['funding_goal']) ? (float)$data['funding_goal'] : 0;
        if ($title === '' || $description === '' || $funding_goal <= 0) {
            jsonError("Title, description and a valid funding goal are required.");
        }
        
        $stmt = $pdo->prepare("INSERT INTO projects (title, description, author_id, funding_goal) VALUES (?, ?, ?, ?)");
        $stmt->execute([$title, $description, $user_id, $funding_goal]);
        echo json_encode(["status" => "success", "message" => "Project submitted for review."]);
    } elseif ($action === 'update_status' && $role === 'admin') {
        $project_id = isset($data['project_id']) ? (int)$data['project_id'] : 0;
        $status = $data['status'] ?? '';
        if ($project_id <= 0 || !in_array($status, ['pending', 'approved', 'rejected'], true)) {
            jsonError("Invalid project or status.");
        }
        
        $stmt = $pdo->prepare("UPDATE projects SET status = ? WHERE id = ?");
        $stmt->execute([$status, $project_id]);
        echo json_encode(["status" => "success", "message" => "Project status updated."]);
    } elseif ($action === 'invest' && $role === 'investor') {
        $project_id = isset($data['project_id']) ? (int)$data['project_id'] : 0;
        $amount = isset($data['amount']) ? (float)$data['amount'] : 0;
        if ($project_id <= 0 || $amount <= 0) {
            jsonError("Invalid project or amount.");
        }

        $projectStmt = $pdo->prepare("SELECT id, status FROM projects WHERE id = ?");
        $projectStmt->execute([$project_id]);
        $project = $projectStmt->fetch(PDO::FETCH_ASSOC);
        if (!$project || $project['status'] !== 'approved') {
            jsonError("Only approved projects can be invested in.");
        }
        
        $stmt = $pdo->prepare("INSERT INTO investments (project_id, investor_id, amount) VALUES (?, ?, ?)");
        $stmt->execute([$project_id, $user_id, $amount]);
        
        // Update project current funding
        $stmt2 = $pdo->prepare("UPDATE projects SET current_funding = current_funding + ? WHERE id = ?");
        $stmt2->execute([$amount, $project_id]);
        
        echo json_encode(["status" => "success", "message" => "Investment added successfully."]);
    } elseif ($action === 'mentor' && $role === 'mentor') {
        $project_id = isset($data['project_id']) ? (int)$data['project_id'] : 0;
        if ($project_id <= 0) {
            jsonError("Invalid project.");
        }

        $projectStmt = $pdo->prepare("SELECT id, status FROM projects WHERE id = ?");
        $projectStmt->execute([$project_id]);
        $project = $projectStmt->fetch(PDO::FETCH_ASSOC);
        if (!$project || $project['status'] !== 'approved') {
            jsonError("Only approved projects can receive mentorship.");
        }
        
        // Check if already mentoring
        $check = $pdo->prepare("SELECT * FROM mentorships WHERE project_id = ? AND mentor_id = ?");
        $check->execute([$project_id, $user_id]);
        if ($check->rowCount() == 0) {
            $stmt = $pdo->prepare("INSERT INTO mentorships (project_id, mentor_id) VALUES (?, ?)");
            $stmt->execute([$project_id, $user_id]);
            echo json_encode(["status" => "success", "message" => "Mentorship offered successfully."]);
        } else {
            echo json_encode(["status" => "error", "message" => "Already mentoring this project."]);
        }
    } elseif ($action === 'update_progress') {
        $project_id = isset($data['project_id']) ? (int)$data['project_id'] : 0;
        $progress = isset($data['progress']) ? (int)$data['progress'] : 0;
        if ($project_id <= 0 || $progress < 0 || $progress > 100) {
            jsonError("Invalid project or progress value (0-100).");
        }

        // Check if user is author or mentor
        $stmt = $pdo->prepare("SELECT author_id FROM projects WHERE id = ?");
        $stmt->execute([$project_id]);
        $project = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$project) {
            jsonError("Project not found.");
        }

        $isAuthor = ($project['author_id'] == $user_id);
        $isMentor = false;
        
        $mStmt = $pdo->prepare("SELECT id FROM mentorships WHERE project_id = ? AND mentor_id = ?");
        $mStmt->execute([$project_id, $user_id]);
        if ($mStmt->rowCount() > 0) {
            $isMentor = true;
        }

        if (!$isAuthor && !$isMentor && $role !== 'admin') {
            jsonError("Unauthorized to update progress.");
        }

        $uStmt = $pdo->prepare("UPDATE projects SET progress = ? WHERE id = ?");
        $uStmt->execute([$progress, $project_id]);
        echo json_encode(["status" => "success", "message" => "Progress updated to $progress%."]);
    } else {
        jsonError("Invalid action or permission denied.");
    }
}
?>
