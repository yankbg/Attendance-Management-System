package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class QRScannerActivity extends AppCompatActivity {
    private static final String TAG = "QRScannerActivity";
    private int teacherId;
    private String teacherName;

    // Camera permission launcher
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startQRScanner();
                } else {
                    Toast.makeText(this, "Camera permission is required to scan QR codes",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            });

    // QR Scanner launcher
    private final ActivityResultLauncher<ScanOptions> qrScannerLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String qrData = result.getContents();
                    Log.d(TAG, "QR Code scanned: " + qrData);
                    handleQRResult(qrData);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check camera permission
        checkCameraPermission();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startQRScanner();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan Student QR Code for Attendance");
        options.setCameraId(0);  // Use rear camera
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(true);
        options.setTimeout(30000);
        qrScannerLauncher.launch(options);
    }

    private void handleQRResult(String qrData) {
        try {
            JSONObject qrJson = new JSONObject(qrData);

            // Validate required fields
            String[] requiredFields = {"studentId", "fullname", "Date", "time"};
            for (String field : requiredFields) {
                if (!qrJson.has(field)) {
                    Toast.makeText(this, "Invalid QR code: missing " + field, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }

            Toast.makeText(this, "QR Code scanned successfully", Toast.LENGTH_SHORT).show();

            // Mark attendance with parsed data
            markAttendance(qrJson);

        } catch (JSONException e) {
            Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void markAttendance(JSONObject qrJson) {
        Toast.makeText(this, "Marking attendance...", Toast.LENGTH_SHORT).show();

        int studentId = Integer.parseInt(qrJson.optString("studentId"));
        String fullname = qrJson.optString("fullname");
        String date = qrJson.optString("Date");
        String time = qrJson.optString("time");


        ApiUtil.markAttendance(this, qrJson.toString(),
                response -> {
                    String status = response.optString("status");
                    String message = response.optString("message");
                    Log.d(TAG, "Attendance API status: " + status + ", message: " + message);

                    if ("success".equalsIgnoreCase(status)) {
                        Toast.makeText(this, "✅ " + message, Toast.LENGTH_LONG).show();

                        // Return success result to calling activity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("attendance_status", "success");
                        resultIntent.putExtra("message", message);
                        resultIntent.putExtra("fullname", fullname);
                        resultIntent.putExtra("student_id", String.valueOf(studentId));

                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(this, "❌ " + message, Toast.LENGTH_LONG).show();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                },
                error -> {
                    Log.e(TAG, "Attendance API error: " + error.getMessage());
                    Toast.makeText(this, "❌ Network error occurred...", Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED);
                    finish();
                });

    }
}