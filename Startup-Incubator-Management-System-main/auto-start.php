<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

$port = 8085;
$jarPath = '';
$isWindows = strtoupper(substr(PHP_OS, 0, 3)) === 'WIN';

// ── Detect JAR path ──────────────────────────────────────────────────────────
if ($isWindows) {
    // Common Windows XAMPP paths
    $possiblePaths = [
        'F:\\xampp\\htdocs\\aoopfinal\\Startup-Incubator-Management-System-main\\backend\\target\\management-0.0.1-SNAPSHOT.jar',
        'C:\\xampp\\htdocs\\Startup-Incubator-Management-System-main\\backend\\target\\management-0.0.1-SNAPSHOT.jar',
        'D:\\xampp\\htdocs\\Startup-Incubator-Management-System-main\\backend\\target\\management-0.0.1-SNAPSHOT.jar',
    ];
} else {
    // Mac / Linux
    $possiblePaths = [
        '/Applications/XAMPP/xamppfiles/htdocs/Startup-Incubator-Management-System-main/backend/target/management-0.0.1-SNAPSHOT.jar',
        '/opt/lampp/htdocs/Startup-Incubator-Management-System-main/backend/target/management-0.0.1-SNAPSHOT.jar',
    ];
}

foreach ($possiblePaths as $path) {
    if (file_exists($path)) {
        $jarPath = $path;
        break;
    }
}

// ── Check if backend is already running ──────────────────────────────────────
function isBackendRunning($port) {
    $connection = @fsockopen('127.0.0.1', $port, $errno, $errstr, 1);
    if ($connection) {
        fclose($connection);
        return true;
    }
    return false;
}

// ── Check Java ───────────────────────────────────────────────────────────────
function javaExists() {
    $output = shell_exec('java -version 2>&1');
    return $output !== null && strlen($output) > 0;
}

// ── Start backend ────────────────────────────────────────────────────────────
function startBackend($jarPath, $isWindows) {
    if (empty($jarPath)) return false;

    if ($isWindows) {
        $cmd = 'start /B "" javaw -jar "' . $jarPath . '" > "%TEMP%\\incubator-backend.log" 2>&1';
        pclose(popen($cmd, 'r'));
    } else {
        $logFile = '/tmp/incubator-backend.log';
        $cmd = 'nohup java -jar "' . $jarPath . '" > "' . $logFile . '" 2>&1 &';
        shell_exec($cmd);
    }
    return true;
}

// ── Main Logic ───────────────────────────────────────────────────────────────
$status = [
    'backend_running' => false,
    'java_found'      => false,
    'jar_found'       => false,
    'started'         => false,
    'message'         => '',
    'os'              => $isWindows ? 'windows' : 'mac/linux',
];

$status['java_found'] = javaExists();
$status['jar_found']  = !empty($jarPath);

if (isBackendRunning($port)) {
    $status['backend_running'] = true;
    $status['message'] = 'Backend is already running.';
} elseif (!$status['java_found']) {
    $status['message'] = 'Java not found. Please run the install script first.';
} elseif (!$status['jar_found']) {
    $status['message'] = 'JAR file not found. Please run the install script first.';
} else {
    $started = startBackend($jarPath, $isWindows);
    $status['started'] = $started;
    $status['message'] = $started ? 'Backend is starting up...' : 'Failed to start backend.';
}

echo json_encode($status);
?>
