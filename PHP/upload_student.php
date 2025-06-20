<?php

ini_set('display_errors', 1);
error_reporting(E_ALL);
// Set JSON response header
header('Content-Type: application/json');

// Enable error reporting for debugging (remove in production)
ini_set('display_errors', 1);
error_reporting(E_ALL);

// 1. Get the raw POST data (JSON)
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// 2. Validate JSON input
if (!$data) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid JSON input'
    ]);
    exit;
}

// 3. Validate required fields
if (empty($data['id']) || empty($data['name']) || empty($data['image'])) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Missing required parameters: id, name, or image'
    ]);
    exit;
}

$id = $data['id'];
$name = $data['name'];
$imageData = $data['image'];

// 4. Clean and decode Base64 image data
if (strpos($imageData, 'base64,') !== false) {
    $imageData = explode('base64,', $imageData)[1];
}
$imageData = str_replace(' ', '+', $imageData);

$decodedImage = base64_decode($imageData);
if ($decodedImage === false) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to decode Base64 image'
    ]);
    exit;
}

// 5. Create uploads directory if it doesn't exist
$uploadDir = __DIR__ . '/uploads/';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0755, true);
}

// 6. Generate a unique filename for the image
$fileName = uniqid('img_') . '.jpg';
$filePath = $uploadDir . $fileName;

// 7. Save the decoded image to the server
if (file_put_contents($filePath, $decodedImage) === false) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to save image on server'
    ]);
    exit;
}

// 8. Database connection parameters
$conn = new mysqli("localhost", "root", "", "AttendanceSystem", 3306);

// 9. Check connection
if ($conn->connect_error) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $conn->connect_error
    ]);
    exit;
}

// 10. Check for duplicate student by id or name
$checkStmt = $conn->prepare("SELECT COUNT(*) FROM students WHERE id = ? OR name = ?");
$checkStmt->bind_param("ss", $id, $name);
$checkStmt->execute();
$checkStmt->bind_result($count);
$checkStmt->fetch();
$checkStmt->close();

if ($count > 0) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Student with this ID or name already registered'
    ]);
    exit;
}

// 11. Insert student data into database
$relativeFilePath = 'uploads/' . $fileName;

$stmt = $conn->prepare("INSERT INTO students (id, name, image_path) VALUES (?, ?, ?)");
if (!$stmt) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database prepare failed: ' . $conn->error
    ]);
    exit;
}

$stmt->bind_param("sss", $id, $name, $relativeFilePath);

if ($stmt->execute()) {
    echo json_encode([
        'status' => 'success',
        'message' => 'Student registered successfully',
        'image_path' => $relativeFilePath
    ]);
} else {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database insert failed: ' . $stmt->error
    ]);
}

$stmt->close();
$conn->close();


?>
