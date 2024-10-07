package com.reivai.todolistapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reivai.todolistapp.R;
import com.reivai.todolistapp.model.Task;
import com.reivai.todolistapp.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private OnItemClickListener listener;
    private List<Task> tasksList = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(Task task);
        void onItemLongClick(Task task);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        Task task = tasksList.get(position);
        holder.tvTitle.setText(task.getTaskName());
        holder.tvDescription.setText(task.getTaskDescription());
        holder.tvDueDate.setText(task.getTaskDate());

        holder.cbComplete.setChecked(task.isCompleted());

        if (task.isCompleted()) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        holder.cbComplete.setOnClickListener(view -> {
            boolean isChecked = holder.cbComplete.isChecked();
            if (isChecked) {
                task.setCompleted(true);
                holder.itemView.setBackgroundColor(Color.LTGRAY);
                Toast.makeText(holder.itemView.getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
            } else {
                task.setCompleted(false);
                holder.itemView.setBackgroundColor(Color.WHITE);
                Toast.makeText(holder.itemView.getContext(), "Task marked as Unfinished", Toast.LENGTH_SHORT).show();
            }

            if (listener != null) {
                listener.onItemLongClick(task);
            }
        });

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(task);
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (listener != null) {
                listener.onItemLongClick(task);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public void submitList(List<Task> tasks) {
        this.tasksList = tasks;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle,  tvDescription, tvDueDate;
        private final CheckBox cbComplete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDueDate = itemView.findViewById(R.id.tvDate);
            cbComplete = itemView.findViewById(R.id.cbComplete);
        }
    }
}
