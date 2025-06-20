<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Get raw JSON input
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, true); // Convert to associative array

// Retrieve date from JSON or fallback to POST/GET
$date = $input['date'] ?? $_POST['date'] ?? $_GET['date'] ?? '';

if (empty($date)) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing date parameter"
    ]);
    exit;
}

// Validate date format (YYYY-MM-DD)
if (!preg_match('/^\d{4}-\d{2}-\d{2}$/', $date)) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid date format. Expected YYYY-MM-DD."
    ]);
    exit;
}

// Database connection
$conn = new mysqli("localhost", "root", "", "AttendanceSystem", 3306);
if ($conn->connect_error) {
    echo json_encode([
        "status" => "error",
        "message" => "Database connection failed: " . $conn->connect_error
    ]);
    exit;
}

// Prepare and execute the query
$stmt = $conn->prepare("SELECT student_id, student_name, time FROM attendance WHERE date = ?");
if (!$stmt) {
    echo json_encode([
        "status" => "error",
        "message" => "Prepare failed: " . $conn->error
    ]);
    $conn->close();
    exit;
}

$stmt->bind_param("s", $date);

if (!$stmt->execute()) {
    echo json_encode([
        "status" => "error",
        "message" => "Execute failed: " . $stmt->error
    ]);
    $stmt->close();
    $conn->close();
    exit;
}

$result = $stmt->get_result();

$attendanceRecords = [];
while ($row = $result->fetch_assoc()) {
    $attendanceRecords[] = $row;
}

$stmt->close();
$conn->close();

echo json_encode([
    "status" => "success",
    "date" => $date,
    "data" => $attendanceRecords
]);
?>
