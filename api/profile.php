<?php
session_start();
require_once 'db.php';

header('Content-Type: application/json');

if (!isset($_SESSION['user_id'])) {
    echo json_encode(["status" => "error", "message" => "Not logged in."]);
    exit;
}

$userId = $_SESSION['user_id'];
$role = $_SESSION['role'] ?? '';
$action = isset($_GET['action']) ? $_GET['action'] : '';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if ($action === 'get') {
        $stmt = $pdo->prepare("SELECT id, name, email, role, photo, age, sex, nid_no, address, degree, contact_no, edit_request_status FROM users WHERE id = ?");
        $stmt->execute([$userId]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);
        
        if ($user) {
            // Fetch project info if the user is a fresher (author)
            $project = null;
            if ($user['role'] === 'fresher') {
                $pStmt = $pdo->prepare("SELECT id, title, status, progress FROM projects WHERE author_id = ? LIMIT 1");
                $pStmt->execute([$userId]);
                $project = $pStmt->fetch(PDO::FETCH_ASSOC);

                if ($project) {
                    // Fetch mentor
                    $mStmt = $pdo->prepare("SELECT u.name FROM mentorships m JOIN users u ON m.mentor_id = u.id WHERE m.project_id = ? LIMIT 1");
                    $mStmt->execute([$project['id']]);
                    $mentor = $mStmt->fetch(PDO::FETCH_ASSOC);
                    $project['mentor'] = $mentor ? $mentor['name'] : 'No mentor yet';

                    // Fetch investors
                    $iStmt = $pdo->prepare("SELECT DISTINCT u.name FROM investments i JOIN users u ON i.investor_id = u.id WHERE i.project_id = ?");
                    $iStmt->execute([$project['id']]);
                    $investors = $iStmt->fetchAll(PDO::FETCH_COLUMN);
                    $project['investors'] = !empty($investors) ? implode(', ', $investors) : 'No investors yet';
                }
            }
            
            echo json_encode(["status" => "success", "user" => $user, "project" => $project]);
        } else {
            echo json_encode(["status" => "error", "message" => "User not found."]);
        }
    } elseif ($action === 'list_requests' && $role === 'admin') {
        $stmt = $pdo->query("SELECT id, name, email, role, edit_request_status FROM users WHERE edit_request_status = 'pending' ORDER BY id DESC");
        $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
        echo json_encode(["status" => "success", "data" => $rows]);
    } else {
        echo json_encode(["status" => "error", "message" => "Invalid action."]);
    }
} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if ($action === 'request_edit') {
        $stmt = $pdo->prepare("UPDATE users SET edit_request_status = 'pending' WHERE id = ?");
        $stmt->execute([$userId]);
        echo json_encode(["status" => "success", "message" => "Request sent to admin."]);
        exit;
    }

    if ($action === 'review_request' && $role === 'admin') {
        $data = json_decode(file_get_contents("php://input"), true);
        if (!is_array($data)) {
            $data = [];
        }
        $targetUserId = isset($data['user_id']) ? (int)$data['user_id'] : 0;
        $status = $data['status'] ?? '';
        if ($targetUserId <= 0 || !in_array($status, ['approved', 'none'], true)) {
            echo json_encode(["status" => "error", "message" => "Invalid request review payload."]);
            exit;
        }
        $stmt = $pdo->prepare("UPDATE users SET edit_request_status = ? WHERE id = ?");
        $stmt->execute([$status, $targetUserId]);
        echo json_encode(["status" => "success", "message" => "Request status updated."]);
        exit;
    }

    if ($action === 'update') {
        // Check if allowed to update
        $stmt = $pdo->prepare("SELECT edit_request_status, age FROM users WHERE id = ?");
        $stmt->execute([$userId]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        // Logic: Allow if status is 'approved' OR if it's the very first time (age is null)
        if ($user['edit_request_status'] !== 'approved' && !empty($user['age'])) {
             echo json_encode(["status" => "error", "message" => "Editing locked. Please request admin approval."]);
             exit;
        }

        $age = $_POST['age'] ?? null;
        $address = $_POST['address'] ?? null;
        $degree = $_POST['degree'] ?? null;
        $contact_no = $_POST['contact_no'] ?? null;
        $sex = $_POST['sex'] ?? null;
        $nid_no = $_POST['nid_no'] ?? null;

        $photoQueryPart = "";
        $params = [$age, $sex, $nid_no, $address, $degree, $contact_no];
        $photoPath = null;
        if (isset($_FILES['photo']) && $_FILES['photo']['error'] === UPLOAD_ERR_OK) {
            $allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
            $detectedType = mime_content_type($_FILES['photo']['tmp_name']);
            if (!in_array($detectedType, $allowedTypes, true)) {
                echo json_encode(["status" => "error", "message" => "Only JPG, PNG, GIF, or WEBP images are allowed."]);
                exit;
            }
            if ($_FILES['photo']['size'] > 2 * 1024 * 1024) {
                echo json_encode(["status" => "error", "message" => "Image size must be 2MB or less."]);
                exit;
            }

            $uploadDir = '../uploads/';
            if (!is_dir($uploadDir)) {
                mkdir($uploadDir, 0777, true);
            }
            $originalName = basename($_FILES['photo']['name']);
            $safeName = preg_replace('/[^a-zA-Z0-9._-]/', '_', $originalName);
            $fileName = time() . '_' . $safeName;
            $uploadFile = $uploadDir . $fileName;
            if (move_uploaded_file($_FILES['photo']['tmp_name'], $uploadFile)) {
                $photoPath = 'uploads/' . $fileName;
                $photoQueryPart = ", photo = ?";
                $params[] = $photoPath;
            }
        }
        $params[] = $userId;
        try {
            // After update, reset status to 'none' to lock it again
            $stmt = $pdo->prepare("UPDATE users SET age = ?, sex = ?, nid_no = ?, address = ?, degree = ?, contact_no = ?, edit_request_status = 'none' $photoQueryPart WHERE id = ?");
            $stmt->execute($params);
            
            // fetch updated photo for response
            $stmt2 = $pdo->prepare("SELECT photo FROM users WHERE id = ?");
            $stmt2->execute([$userId]);
            $photoRow = $stmt2->fetch(PDO::FETCH_ASSOC);
            $photo = $photoRow ? $photoRow['photo'] : null;
            
            echo json_encode(["status" => "success", "message" => "Profile updated successfully.", "photo" => $photo]);
        } catch(PDOException $e) {
            echo json_encode(["status" => "error", "message" => "Error updating profile."]);
        }
    }
}
?>
