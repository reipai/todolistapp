package com.reivai.todolistapp.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.reivai.todolistapp.database.TaskDao;
import com.reivai.todolistapp.database.TaskDatabase;
import com.reivai.todolistapp.model.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> taskModels;

    public TaskRepository(Application application) {
        TaskDatabase db = TaskDatabase.getInstance(application);
        taskDao = db.taskDao();
        taskModels = taskDao.getAllTasks();
    }

    public void insert(Task task) {
        new InsertTaskAsync(taskDao).execute(task);
    }

    public void update(Task task) {
        new UpdateTaskAsync(taskDao).execute(task);
    }

    public void deleteById(int taskId) {
        new DeleteTaskByIdAsync(taskDao).execute(taskId);
    }

    public LiveData<List<Task>> getTaskModels() {
        return taskModels;
    }

    private static class InsertTaskAsync extends AsyncTask<Task, Void, Void> {
        private final TaskDao taskDao;

        private InsertTaskAsync(TaskDao taskDao) {
            this.taskDao = taskDao;
        }


        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.insertTask(tasks[0]);
            return null;
        }
    }

    private static class UpdateTaskAsync extends AsyncTask<Task, Void, Void> {
        private final TaskDao taskDao;

        private UpdateTaskAsync(TaskDao taskDao) {
            this.taskDao = taskDao;
        }


        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.updateTask(tasks[0]);
            return null;
        }
    }

    private static class DeleteTaskByIdAsync extends AsyncTask<Integer, Void, Void> {
        private final TaskDao taskDao;

        private DeleteTaskByIdAsync(TaskDao taskDao) {
            this.taskDao = taskDao;
        }


        @Override
        protected Void doInBackground(Integer... ids) {
            taskDao.deleteById(ids[0]);
            return null;
        }
    }

}
