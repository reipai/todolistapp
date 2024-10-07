package com.reivai.todolistapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.reivai.todolistapp.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Query("DELETE FROM tasks WHERE id = :taskId")
    void deleteById(int taskId);

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    LiveData<List<Task>> getAllTasks();
}
