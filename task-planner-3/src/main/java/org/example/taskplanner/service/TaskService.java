package org.example.taskplanner.service;

import org.example.taskplanner.model.Task;
import org.example.taskplanner.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {

    private TaskRepository repository;

    @Autowired
    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> getAll() {
        return repository.findAll();
    }

    public void add(Task task) {
        repository.save(task);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    public void markCompleted(Long id) {
        repository.findById(id).ifPresent(t -> t.setCompleted(true));
    }

    public List<Task> sortByPriority() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(Task::getPriority))
                .toList();
    }

    public List<Task> sortByDate() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(Task::getDueDate))
                .toList();
    }
}
