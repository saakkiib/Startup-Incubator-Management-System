<?php
$dbname = "incubator_db";
$user   = "root";
$password = "";

try {
    $pdo = new PDO(
        "mysql:host=127.0.0.1;port=3306;dbname=$dbname;charset=utf8mb4",
        $user,
        $password
    );
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch(PDOException $e) {
    die(json_encode(["status" => "error", "message" => "Database connection failed: " . $e->getMessage()]));
}
?>
