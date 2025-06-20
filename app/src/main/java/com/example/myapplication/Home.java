package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private TextView tvWelcome;
    private Button btnScanQR, btnViewAttendance,btnReg;


    // Activity result launcher for QR scanner
    private final ActivityResultLauncher<Intent> qrScannerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String status = result.getData().getStringExtra("attendance_status");
                    String message = result.getData().getStringExtra("message");

                    String studentName = result.getData().getStringExtra("fullname");
                    if (studentName == null) {

                        studentName = result.getData().getStringExtra("fullname");
                    }
                    String studentId = result.getData().getStringExtra("student_id");

                    Log.d(TAG, "Attendance result - Status: " + status + ", Message: " + message);

                    if ("success".equals(status)) {
                        showAttendanceSuccessDialog(message, studentName, studentId);
                        Toast.makeText(this, "✅ " + message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "❌ Attendance failed", Toast.LENGTH_LONG).show();
                    }
                }
            });


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnScanQR = findViewById(R.id.btnScanQR);
        btnViewAttendance = findViewById(R.id.btnViewAttendance);
        btnReg = findViewById(R.id.Regbtn);

        tvWelcome.setText("Welcome,Teacher!");

        btnScanQR.setOnClickListener(v -> startQRScanner());
        btnViewAttendance.setOnClickListener(v -> viewAttendance2());
        btnReg.setOnClickListener(v -> register());
    }

    private void startQRScanner() {
        Log.d(TAG, "Starting QR scanner");
        Intent intent = new Intent(this, QRScannerActivity.class);
        qrScannerLauncher.launch(intent);
    }

    private void showAttendanceSuccessDialogWithImage(String message, String studentName, String studentId, String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✅ Attendance Marked Successfully!");

        // Inflate a custom layout containing TextViews and an ImageView
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_attendance_success, null);

        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        TextView tvName = dialogView.findViewById(R.id.tvName);
        TextView tvId = dialogView.findViewById(R.id.tvId);
        ImageView ivStudentImage = dialogView.findViewById(R.id.ivStudentImage);

        tvMessage.setText(message);
        tvName.setText("Student: " + studentName);
        tvId.setText("Student ID: " + studentId);

        // Load the image using Glide or Picasso (Glide example)
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(ivStudentImage);
        } else {
            ivStudentImage.setImageResource(R.drawable.download);
        }


        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.show();
    }



    public void register(){
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }

    private void showAttendanceSuccessDialog(String message, String studentName, String studentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✅ Attendance Marked Successfully!");

        StringBuilder dialogMessage = new StringBuilder();
        dialogMessage.append(message);
        if (studentName != null && !studentName.isEmpty()) {
            dialogMessage.append("\nStudent: ").append(studentName);
        }
        if (studentId != null && !studentId.isEmpty()) {
            dialogMessage.append("\nStudent ID: ").append(studentId);
        }
        dialogMessage.append("\n\nThank you!");

        builder.setMessage(dialogMessage.toString());
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.setIcon(android.R.drawable.ic_dialog_info);


        ApiUtil.getStudentInfo(this, studentId, studentName,
                response -> {
                    String imageUrl = null;
                    if ("success".equals(response.optString("status"))) {
                        imageUrl = ApiUtil.BASE_URL + response.optString("image_path");

                    }
                    showAttendanceSuccessDialogWithImage(message, studentName, studentId, imageUrl);
                },
                error -> {
                    // Fallback: Show dialog without image if image fetch fails
                    String urlerror = "C:\\Users\\HP\\AndroidStudioProjects\\MyApplication2\\app\\src\\main\\res\\drawable\\error2.jpeg";
                    showAttendanceSuccessDialogWithImage(message, studentName, studentId,  urlerror);
                }
        );

    }

    public void viewAttendance2(){
            Intent i = new Intent(this,AttendanceView.class);
            startActivity(i);
    }



}
