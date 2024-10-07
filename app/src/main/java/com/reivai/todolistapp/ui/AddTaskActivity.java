package com.reivai.todolistapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.reivai.todolistapp.R;
import com.reivai.todolistapp.databinding.ActivityAddTaskBinding;
import com.reivai.todolistapp.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private ActivityAddTaskBinding binding;
    private TaskViewModel viewModel;
    private boolean isCompleted = false;
    int taskId = -1;
    String title, description, dueDate;

    public static final String EXTRA_ID = "com.reivai.todolistapp.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.reivai.todolistapp.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.reivai.todolistapp.EXTRA_DESCRIPTION";
    public static final String EXTRA_DATE = "com.reivai.todolistapp.EXTRA_DATE";
    public static final String EXTRA_IS_COMPLETED = "com.reivai.todolistapp.EXTRA_IS_COMPLETED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle("");
        }

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)) {
            binding.toolbar.setTitle("Edit Task");
            binding.etTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            binding.etDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            binding.etDuedate.setText(intent.getStringExtra(EXTRA_DATE));
            taskId = intent.getIntExtra(EXTRA_ID, -1);
        } else {
            binding.toolbar.setTitle("Add Task");
        }

        binding.etDuedate.setOnClickListener(view -> pickDate());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveTask();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            deleteTask();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pickDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String selectedDate = sdf.format(calendar.getTime());

            binding.etDuedate.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveTask() {
        title = binding.etTitle.getText().toString();
        description = binding.etDescription.getText().toString();
        dueDate = binding.etDuedate.getText().toString();
        isCompleted = false;

        if (title.trim().isEmpty() || description.trim().isEmpty() || dueDate.equals("No Date Selected")) {
            Toast.makeText(this, "Please insert a title, description and select a due date", Toast.LENGTH_SHORT).show();
            return;
        } else if (title.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title", Toast.LENGTH_SHORT).show();
            return;
        } else if (description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a description", Toast.LENGTH_SHORT).show();
            return;
        } else if (dueDate.trim().isEmpty()) {
            Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_DATE, dueDate);
        data.putExtra(EXTRA_IS_COMPLETED, isCompleted);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        Log.d("wakacaw", "saveTask: " + title + "," + description);
        finish();
    }

    private void deleteTask() {
        if (taskId == -1) {
            Toast.makeText(this, "No task to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure to delete this task?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    viewModel.deleteById(taskId);

                    Intent data = new Intent();
                    data.putExtra(EXTRA_ID, taskId);
                    setResult(RESULT_FIRST_USER, data);

                    finish();
                    Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}