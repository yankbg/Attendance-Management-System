<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database connection
$conn = new mysqli("localhost", "root", "", "AttendanceSystem", 3306);
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "DB connection failed: " . $conn->connect_error]);
    exit;
}

// Parse JSON or form-data input
$input = json_decode(file_get_contents('php://input'), true);
$id = $input['student_id'] ?? $_POST['student_id'] ?? '';
$name = $input['student_name'] ?? $_POST['student_name'] ?? '';

// Validate inputs
if (empty($id) || !is_numeric($id) || empty($name)) {
    echo json_encode(["status" => "error", "message" => "Invalid or missing student_id or student_name"]);
    exit;
}

// Prepare statement with case-insensitive name comparison
$lowerName = strtolower($name);
$stmt = $conn->prepare("SELECT image_path FROM students WHERE id = ? AND LOWER(name) = ?");
if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Prepare failed: " . $conn->error]);
    $conn->close();
    exit;
}

$stmt->bind_param("is", $id, $lowerName);

if (!$stmt->execute()) {
    echo json_encode(["status" => "error", "message" => "Execute failed: " . $stmt->error]);
    $stmt->close();
    $conn->close();
    exit;
}

$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode(["status" => "success", "image_path" => $row['image_path']]);
} else {
    echo json_encode(["status" => "error", "message" => "Image not found"]);
}

$stmt->close();
$conn->close();
?>
