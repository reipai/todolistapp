package com.reivai.todolistapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.reivai.todolistapp.model.Task;
import com.reivai.todolistapp.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository repository;
    private final LiveData<List<Task>> taskLiveData;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        taskLiveData = repository.getTaskModels();
    }

    public void insert(Task task) {
        repository.insert(task);
    }

    public void update(Task task) {
        repository.update(task);
    }

    public void deleteById(int id ) {
        repository.deleteById(id);
    }

    public LiveData<List<Task>> getAllTasks() {
        return taskLiveData;
    }
}
