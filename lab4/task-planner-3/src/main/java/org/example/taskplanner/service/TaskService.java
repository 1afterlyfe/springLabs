package org.example.taskplanner.service;

import org.example.taskplanner.model.Task;
import org.example.taskplanner.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;

    @Autowired
    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> getAll() {
        return repository.findAll();
    }

    public Optional<Task> getById(Long id) {
        return repository.findById(id);
    }

    public Task create(Task task) {
        task.setCompleted(task.isCompleted());
        return repository.save(task);
    }

    public Task update(Long id, Task updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setDueDate(updated.getDueDate());
                    existing.setPriority(updated.getPriority());
                    existing.setCompleted(updated.isCompleted());
                    return repository.save(existing);
                })
                .orElse(null);
    }

    public boolean delete(Long id) {
        if (repository.findById(id).isPresent()) {
            repository.delete(id);
            return true;
        }
        return false;
    }

    public void markCompleted(Long id) {
        repository.findById(id).ifPresent(t -> {
            t.setCompleted(true);
            repository.save(t);
        });
    }

    public List<Task> filter(String title, String priority, Boolean completed) {
        return repository.findAll().stream()
                .filter(t -> title == null || t.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(t -> priority == null || t.getPriority().equalsIgnoreCase(priority))
                .filter(t -> completed == null || t.isCompleted() == completed)
                .sorted(Comparator.comparing(Task::getDueDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
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
    public Task add(Task task) {
        return create(task);
    }
}
