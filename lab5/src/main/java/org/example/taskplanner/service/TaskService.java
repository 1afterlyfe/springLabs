package org.example.taskplanner.service;

import org.example.taskplanner.model.Task;
import org.example.taskplanner.repository.TaskDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskDao taskDao;

    @Autowired
    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public List<Task> getAll() {
        return taskDao.findAll();
    }

    public Optional<Task> getById(Long id) {
        return taskDao.read(id);
    }

    public Task create(Task task) {
        Long id = taskDao.create(task);
        task.setId(id);
        return task;
    }

    public Task update(Long id, Task updated) {
        return taskDao.read(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setDueDate(updated.getDueDate());
                    existing.setPriority(updated.getPriority());
                    existing.setCompleted(updated.isCompleted());
                    taskDao.update(existing);
                    return existing;
                })
                .orElse(null);
    }

    public boolean delete(Long id) {
        if (taskDao.read(id).isPresent()) {
            taskDao.delete(id);
            return true;
        }
        return false;
    }

    public void markCompleted(Long id) {
        taskDao.read(id).ifPresent(t -> {
            t.setCompleted(true);
            taskDao.update(t);
        });
    }

    @Transactional
    public void completeMultipleTasks(List<Long> ids) {
        for (Long id : ids) {
            Task task = taskDao.read(id)
                    .orElseThrow(() -> new RuntimeException("Task with ID " + id + " not found. Transaction rolled back."));
            task.setCompleted(true);
            taskDao.update(task);
        }
    }

    public List<Task> filter(String title, String priority, Boolean completed) {
        return taskDao.findAll().stream()
                .filter(t -> title == null || t.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(t -> priority == null || t.getPriority().equalsIgnoreCase(priority))
                .filter(t -> completed == null || t.isCompleted() == completed)
                .sorted(Comparator.comparing(Task::getDueDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public List<Task> sortByPriority() {
        return taskDao.findAll().stream()
                .sorted(Comparator.comparing(Task::getPriority))
                .toList();
    }

    public List<Task> sortByDate() {
        return taskDao.findAll().stream()
                .sorted(Comparator.comparing(Task::getDueDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }
}