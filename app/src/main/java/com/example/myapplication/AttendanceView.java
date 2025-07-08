package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class AttendanceView extends AppCompatActivity {

    ArrayList<RecordAttendance> attendanceList = new ArrayList<>();
    RecyclerView rvAttendanceRecords;
    TextView tvEmpty;
    CalendarView calendarView;
    Calendar calendar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_view);

        rvAttendanceRecords = findViewById(R.id.rvAttendanceRecords);
        tvEmpty = findViewById(R.id.tvEmpty);
        calendarView = findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();
        viewAttendance();
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int months = currentDate.getMonthValue();  // 1 (January) to 12 (December)
        int day = currentDate.getDayOfMonth();
        setDate(year,months,day);
        getDate();

            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int year,int month,int day){
                    String Date;
                    int Month = (month +1);

                    if(Month <= 9 || day<=9){
                        if(Month <= 9 && day<=9){
                            Date = year + "-0" + Month + "-0"+day;
                            attendanceViewDate(Date);
                            Toast.makeText(AttendanceView.this, Date, Toast.LENGTH_SHORT).show();
                        } else if (Month <= 9 && day > 9) {
                            Date = year + "-0" + Month + "-"+day;
                            attendanceViewDate(Date);
                            Toast.makeText(AttendanceView.this, Date, Toast.LENGTH_SHORT).show();
                        } else if (Month > 9 && day <= 9) {
                            Date = year + "-" + Month + "-0"+day;
                            attendanceViewDate(Date);
                            Toast.makeText(AttendanceView.this, Date, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                         Date = year + "-" + Month +"-"+day;
                        attendanceViewDate(Date);
                        Toast.makeText(AttendanceView.this, Date, Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }
    public void setDate(int year, int month, int day){
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month -1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long milli = calendar.getTimeInMillis();
        calendarView.setDate(milli);
    }
    public void getDate(){
        long date = calendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        calendar.setTimeInMillis(date);
        String Date  = simpleDateFormat.format(calendar.getTime());
        Toast.makeText(this, Date, Toast.LENGTH_SHORT).show();
    }
    private void viewAttendance() {
        String url = "https://attendancesystemrail-production.up.railway.app/get_attendance";

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
        String url = "https://attendancesystemrail-production.up.railway.app/get_attendance_date";

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