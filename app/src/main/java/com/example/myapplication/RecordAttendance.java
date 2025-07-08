package com.example.myapplication;

public class RecordAttendance {
    public int studentId;
    public String studentName;
    public String date;
    public String time;


    public RecordAttendance(int studentId, String studentName, String date, String time) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.date = date;
        this.time = time;

    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getDate() {
        return date.substring(0,10);
    }

    public String getTime() {
        return time;
    }
}
