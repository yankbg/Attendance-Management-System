<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

// Handle preflight OPTIONS request for CORS
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode([
        "status" => "error",
        "message" => "Only POST requests are allowed"
    ]);
    exit;
}

// Parse input data depending on Content-Type
$contentType = $_SERVER["CONTENT_TYPE"] ?? '';
if (stripos($contentType, 'application/json') !== false) {
    $inputJSON = file_get_contents('php://input');
    $input = json_decode($inputJSON, true);
    $studentId = $input['studentId'] ?? '';
    $fullname = $input['fullname'] ?? '';
} else {
    // Fallback to form-urlencoded data
    $studentId = $_POST['studentId'] ?? '';
    $fullname = $_POST['fullname'] ?? '';
}

// Validate inputs
if (empty($studentId) || empty($fullname)) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing studentId or fullname"
    ]);
    exit;
}

if (!is_numeric($studentId)) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid studentId"
    ]);
    exit;
}

// Connect to database
$conn = new mysqli("localhost", "root", "", "AttendanceSystem", 3306);
if ($conn->connect_error) {
    echo json_encode([
        "status" => "error",
        "message" => "Database connection failed: " . $conn->connect_error
    ]);
    exit;
}

// Prepare and execute query (case-insensitive search on name)
$lowerFullname = strtolower($fullname);
$stmt = $conn->prepare("SELECT id, image_path FROM students WHERE id = ? AND LOWER(name) = ?");
if (!$stmt) {
    echo json_encode([
        "status" => "error",
        "message" => "Prepare failed: " . $conn->error
    ]);
    $conn->close();
    exit;
}

$stmt->bind_param("is", $studentId, $lowerFullname);

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

if ($row = $result->fetch_assoc()) {
    echo json_encode([
        "status" => "success",
        "exists" => true,
        "image_path" => $row['image_path'] // Provide relative or absolute URL to student image
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "exists" => false,
        "message" => "Student not found"
    ]);
}

$stmt->close();
$conn->close();
?>
