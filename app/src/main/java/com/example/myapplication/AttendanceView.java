package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AttendanceView extends AppCompatActivity {

    ArrayList<RecordAttendance> attendanceList = new ArrayList<>();
    RecyclerView rvAttendanceRecords;
    TextView tvEmpty;
    Button btnSearch;
    EditText etSearchDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_view);

        rvAttendanceRecords = findViewById(R.id.rvAttendanceRecords);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnSearch = findViewById(R.id.btnSearch);
         etSearchDate = findViewById(R.id.etSearchDate);
        viewAttendance();
        btnSearch.setOnClickListener(v -> {
            String date = etSearchDate.getText().toString().trim();
            if (date.isEmpty()) {
                // Load all attendance or show message
                viewAttendance(); // or a method that loads all
            } else {
                attendanceViewDate(date);
            }
        });

    }
    private void viewAttendance() {
        String url = "http://10.0.2.2/attendance_system2/get_attendance.php";

        Toast.makeText(this, "Loading attendance...", Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.optString("status");
                        if ("success".equalsIgnoreCase(status)) {
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            attendanceList.clear();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                int studentId = obj.getInt("student_id");
                                String studentName = obj.getString("student_name");
                                String date = obj.getString("date");
                                String time = obj.getString("time");

                                attendanceList.add(new RecordAttendance(studentId, studentName, date, time));
                            }

                            if (attendanceList.isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                rvAttendanceRecords.setVisibility(View.GONE);
                            } else {
                                tvEmpty.setVisibility(View.GONE);
                                rvAttendanceRecords.setVisibility(View.VISIBLE);
                                attendanceAdapter attendanceAdapter = new attendanceAdapter(attendanceList);
                                rvAttendanceRecords.setLayoutManager(new LinearLayoutManager(this));
                                rvAttendanceRecords.setAdapter(attendanceAdapter);
                                attendanceAdapter.setAttendanceList(attendanceList);
                                attendanceAdapter.notifyDataSetChanged();
                            }

                        } else {
                            String message = jsonResponse.optString("message", "Failed to load attendance");
                            Toast.makeText(this, "❌ " + message, Toast.LENGTH_LONG).show();
                            tvEmpty.setVisibility(View.VISIBLE);
                            rvAttendanceRecords.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "❌ JSON parsing error", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvAttendanceRecords.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Toast.makeText(this, "❌ Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvAttendanceRecords.setVisibility(View.GONE);
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void attendanceViewDate(String date) {
        String url = "http://10.0.2.2/attendance_system2/get_attendanceDate.php";

        Toast.makeText(this, "Loading attendance for " + date + "...", Toast.LENGTH_SHORT).show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        String status = response.optString("status");
                        if ("success".equalsIgnoreCase(status)) {
                            JSONArray dataArray = response.getJSONArray("data");
                            attendanceList.clear();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                int studentId = obj.getInt("student_id");
                                String studentName = obj.getString("student_name");
                                String time = obj.getString("time");

                                // Assuming your RecordAttendance constructor supports date and time
                                attendanceList.add(new RecordAttendance(studentId, studentName, date, time));
                            }

                            if (attendanceList.isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                rvAttendanceRecords.setVisibility(View.GONE);
                            } else {
                                tvEmpty.setVisibility(View.GONE);
                                attendanceAdapter attendanceAdapter = new attendanceAdapter(attendanceList);
                                rvAttendanceRecords.setLayoutManager(new LinearLayoutManager(this));
                                rvAttendanceRecords.setAdapter(attendanceAdapter);
                                rvAttendanceRecords.setVisibility(View.VISIBLE);
                                attendanceAdapter.setAttendanceList(attendanceList);
                                attendanceAdapter.notifyDataSetChanged();
                            }
                        } else {
                            String message = response.optString("message", "Failed to load attendance");
                            Toast.makeText(this, "❌ " + message, Toast.LENGTH_LONG).show();
                            tvEmpty.setVisibility(View.VISIBLE);
                            rvAttendanceRecords.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "❌ JSON parsing error", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvAttendanceRecords.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Toast.makeText(this, "❌ Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvAttendanceRecords.setVisibility(View.GONE);
                });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

}