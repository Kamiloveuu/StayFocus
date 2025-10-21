package com.example.stayfocus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryRecord> records;

    public HistoryAdapter(List<HistoryRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryRecord record = records.get(position);

        // Set data ke views
        holder.tvActivityName.setText(record.getActivityName());
        holder.tvDate.setText("Date: " + record.getDate());
        holder.tvTime.setText("Time: " + record.getTime());
        holder.tvSessionType.setText(record.getSessionType());

        // Set loop info dengan styling berbeda berdasarkan konten
        if (record.getLoopInfo() != null && !record.getLoopInfo().isEmpty()) {
            holder.tvLoopInfo.setText(record.getLoopInfo());
            holder.tvLoopInfo.setVisibility(View.VISIBLE);

            // Styling berdasarkan jenis loop info
            if (record.getLoopInfo().contains("Completed")) {
                holder.tvLoopInfo.setBackgroundColor(holder.itemView.getContext()
                        .getResources().getColor(R.color.green));
                holder.tvLoopInfo.setTextColor(holder.itemView.getContext()
                        .getResources().getColor(R.color.white));
            } else if (record.getLoopInfo().contains("Loop")) {
                holder.tvLoopInfo.setBackgroundColor(holder.itemView.getContext()
                        .getResources().getColor(R.color.light_blue));
                holder.tvLoopInfo.setTextColor(holder.itemView.getContext()
                        .getResources().getColor(R.color.blue));
            }
        } else {
            holder.tvLoopInfo.setVisibility(View.GONE);
        }

        // Styling session type berdasarkan jenis session
        if (record.getSessionType().contains("Work")) {
            holder.tvSessionType.setBackgroundColor(holder.itemView.getContext()
                    .getResources().getColor(R.color.purple));
            holder.tvSessionType.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(R.color.purple));
        } else if (record.getSessionType().contains("Break")) {
            holder.tvSessionType.setBackgroundColor(holder.itemView.getContext()
                    .getResources().getColor(R.color.light_blue));
            holder.tvSessionType.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(R.color.light_blue));
        } else {
            holder.tvSessionType.setBackgroundColor(holder.itemView.getContext()
                    .getResources().getColor(R.color.gray));
            holder.tvSessionType.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void updateData(List<HistoryRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.records.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivityName, tvDate, tvTime, tvSessionType, tvLoopInfo;

        ViewHolder(View itemView) {
            super(itemView);
            tvActivityName = itemView.findViewById(R.id.tvActivityName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSessionType = itemView.findViewById(R.id.tvSessionType);
            tvLoopInfo = itemView.findViewById(R.id.tvLoopInfo);
        }
    }
}