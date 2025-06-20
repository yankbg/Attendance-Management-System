<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}
try {
    // Database connection - update credentials as needed
    $conn = new mysqli("localhost", "root", "", "AttendanceSystem", 3306);
    if ($conn->connect_error) {
        throw new Exception("Database connection failed: " . $conn->connect_error);
    }

    // Query attendance records sorted by date descending
    $sql = "SELECT student_name, student_id, date, time FROM attendance ORDER BY date DESC, time DESC";
    $result = $conn->query($sql);

if (!$result) {
    throw new Exception("Query failed: " . $conn->error);
}


    $attendance_records = [];
    while ($row = $result->fetch_assoc()) {
        $attendance_records[] = [
            "student_name" => $row['student_name'],
            "student_id" => intval($row['student_id']),
            "date" => $row['date'],
            "time" => $row['time']
        ];
    }

    if (empty($attendance_records)) {
        echo json_encode([
            "status" => "success",
            "message" => "No attendance records found",
            "data" => [],
            "count" => 0
        ]);
    } else {
        echo json_encode([
            "status" => "success",
            "message" => "Attendance records retrieved successfully",
            "data" => $attendance_records,
            "count" => count($attendance_records)
        ]);
    }


    $conn->close();
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
?>
