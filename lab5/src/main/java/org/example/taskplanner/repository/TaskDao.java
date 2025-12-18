package org.example.taskplanner.repository;

import org.example.taskplanner.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskDao {
    Long create(Task task);

    Optional<Task> read(Long id);

    void update(Task task);

    void delete(Long id);

    List<Task> findAll();

    List<Task> findByTitle(String title);
}