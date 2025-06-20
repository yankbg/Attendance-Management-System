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

// For GET requests (optional testing)
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    echo json_encode([
        "status" => "info",
        "message" => "This endpoint expects POST requests with attendance data",
        "data_received" => $data
    ]);
    exit;
}


try {
    // Get POST data
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
        
    if (!$data || !isset($data['qr_data'])) {
        throw new Exception("Missing 'qr_data' in request");
    }

    // qr_data is a JSON string, decode it
    $qr_info = json_decode($data['qr_data'], true);

    if (!$qr_info) {
        throw new Exception("Invalid JSON format in 'qr_data'");
    }

    // Validate required fields in QR data
    
    $required_fields = ['studentId', 'fullname', 'Date', 'time'];
    foreach ($required_fields as $field) {
        if (empty($qr_info[$field])) {
            throw new Exception("Missing required field: " . $field);
        }
    }

    $student_id = intval($qr_info['studentId']);
    $student_name = $qr_info['fullname'];
    $date = $qr_info['Date']; // Expected format: YYYY-MM-DD
    $time = $qr_info['time']; // Expected format: HH:MM:SS or HH:MM

    // Database connection - update credentials accordingly
    $conn = new mysqli("localhost", "root", "", "AttendanceSystem", 3306);

    if ($conn->connect_error) {
        throw new Exception("Database connection failed: " . $conn->connect_error);
    }

    // Create attendance table if not exists
    $create_table_sql = "CREATE TABLE IF NOT EXISTS attendance (
        id INT AUTO_INCREMENT PRIMARY KEY,
        student_id INT NOT NULL,
        student_name VARCHAR(255) NOT NULL,
        date DATE NOT NULL,
        time TIME NOT NULL,
        marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        UNIQUE KEY unique_attendance (student_id, date)
    )";

    if (!$conn->query($create_table_sql)) {
        throw new Exception("Error creating attendance table: " . $conn->error);
    }

    // Check if attendance already marked for student on the date
    $check_sql = "SELECT id FROM attendance WHERE student_id = ? AND date = ?";
    $stmt = $conn->prepare($check_sql);
    $stmt->bind_param("is", $student_id, $date);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        echo json_encode([
            "status" => "error",
            "message" => "Attendance already marked for this student on " . $date
        ]);
        $stmt->close();
        $conn->close();
        exit;
    }
    $stmt->close();

    // Insert attendance record
    $insert_sql = "INSERT INTO attendance (student_id, student_name, date, time) VALUES (?, ?, ?, ?)";
    $stmt = $conn->prepare($insert_sql);
    $stmt->bind_param("isss", $student_id, $student_name, $date, $time);

    if ($stmt->execute()) {
        echo json_encode([
            "status" => "success",
            "message" => "Attendance marked successfully for student " . $student_name,
            "data" => [
                "studentId" => $student_id,
                "fullname" => $student_name,
                "Date" => $date,
                "time" => $time,
                "marked_at" => date('Y-m-d H:i:s')
            ]
        ]);
    } else {
        throw new Exception("Failed to mark attendance: " . $stmt->error);
    }

    $stmt->close();
    $conn->close();

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
?>
