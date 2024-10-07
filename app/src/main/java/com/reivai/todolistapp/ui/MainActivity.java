package com.reivai.todolistapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.reivai.todolistapp.R;
import com.reivai.todolistapp.adapter.TaskAdapter;
import com.reivai.todolistapp.databinding.ActivityMainBinding;
import com.reivai.todolistapp.model.Task;
import com.reivai.todolistapp.viewmodel.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private ActivityMainBinding binding;
    private TaskAdapter adapter;
    private final List<Task> taskList = new ArrayList<>();
    public int EDIT_TASK_REQUEST = 1;
    public int ADD_TASK_REQUEST = 2;
    String title, description, duedate;
    boolean isCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.rvTask.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTask.setHasFixedSize(true);

        adapter = new TaskAdapter();
        binding.rvTask.setAdapter(adapter);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, taskModels -> {
            taskList.clear();
            taskList.addAll(taskModels);
            adapter.submitList(new ArrayList<>(taskList));
        });

        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                intent.putExtra(AddTaskActivity.EXTRA_ID, task.getId());
                intent.putExtra(AddTaskActivity.EXTRA_TITLE, task.getTaskName());
                intent.putExtra(AddTaskActivity.EXTRA_DESCRIPTION, task.getTaskDescription());
                intent.putExtra(AddTaskActivity.EXTRA_DATE, task.getTaskDate());
                intent.putExtra(AddTaskActivity.EXTRA_IS_COMPLETED, task.isCompleted());
                startActivityForResult(intent, EDIT_TASK_REQUEST);
            }

            @Override
            public void onItemLongClick(Task task) {
                taskViewModel.update(task);
                adapter.notifyDataSetChanged();
            }
        });

        binding.addTask.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            startActivityForResult(intent, ADD_TASK_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            title = data.getStringExtra(AddTaskActivity.EXTRA_TITLE);
            description = data.getStringExtra(AddTaskActivity.EXTRA_DESCRIPTION);
            duedate = data.getStringExtra(AddTaskActivity.EXTRA_DATE);
            isCompleted = data.getBooleanExtra(AddTaskActivity.EXTRA_IS_COMPLETED, false);

            if (requestCode == ADD_TASK_REQUEST) {
                Task task = new Task(title, description, duedate, isCompleted);
                taskViewModel.insert(task);
                Toast.makeText(this, "Task has been successfully added", Toast.LENGTH_SHORT).show();
            } else if (requestCode == EDIT_TASK_REQUEST) {
                int id = data.getIntExtra(AddTaskActivity.EXTRA_ID, -1);
                if (id != -1) {
                    Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
                    Task task = new Task(title, description, duedate, isCompleted);
                    task.setId(id);
                    taskViewModel.update(task);
                }
            }
            Log.d("wakacaw", "data: " + title + " - " + description);
        } else if (resultCode == RESULT_FIRST_USER && data != null) {
            int deletedId = data.getIntExtra(AddTaskActivity.EXTRA_ID, -1);
            if (deletedId != -1) {
                taskViewModel.deleteById(deletedId);
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filterTasks(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterTasks(s);
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            adapter.submitList(new ArrayList<>(taskList));
            return false;
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void filterTasks(String query) {
        String searchQuery = query.toLowerCase().trim();

        taskViewModel.getAllTasks().observe(this, taskModels -> {
            List<Task> filteredTasks = new ArrayList<>();
            for (Task task : taskModels) {
                if (task.getTaskName().toLowerCase().contains(searchQuery)) {
                    filteredTasks.add(task);
                }
            }
            adapter.submitList(filteredTasks);
        });
    }
}