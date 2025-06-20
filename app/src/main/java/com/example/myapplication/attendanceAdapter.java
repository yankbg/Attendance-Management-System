package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class attendanceAdapter extends RecyclerView.Adapter<attendanceAdapter.MyViewHolder>{

    private ArrayList<RecordAttendance> attendanceList;

    public attendanceAdapter(ArrayList<RecordAttendance> attendanceList) {
        this.attendanceList = attendanceList;
    }
    @NonNull
    @Override
    public attendanceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_row, parent, false);
        return new attendanceAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull attendanceAdapter.MyViewHolder holder, int position) {
        holder.tvStudentName.setText(attendanceList.get(position).getStudentName());
        holder.tvStudentId.setText(String.valueOf(attendanceList.get(position).getStudentId()));
        holder.tvDate.setText(attendanceList.get(position).getDate());
        holder.tvTime.setText(attendanceList.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }
    public  void setAttendanceList(ArrayList<RecordAttendance> newList) {
        attendanceList = newList;
        notifyDataSetChanged();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvStudentName,tvStudentId,tvDate,tvTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);

        }
    }

}
