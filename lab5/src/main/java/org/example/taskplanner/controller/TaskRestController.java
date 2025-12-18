package org.example.taskplanner.controller;

import org.example.taskplanner.dto.PagedResponse;
import org.example.taskplanner.dto.TaskRequest;
import org.example.taskplanner.dto.TaskResponse;
import org.example.taskplanner.model.Task;
import org.example.taskplanner.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {

    private final TaskService taskService;

    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setCompleted(request.getCompleted() != null && request.getCompleted());

        Task created = taskService.create(task);
        return ResponseEntity
                .created(URI.create("/api/tasks/" + created.getId()))
                .body(TaskResponse.from(created)); // Повертає 201 та згенерований ID
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TaskResponse>> getTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        List<Task> filtered = taskService.filter(title, priority, completed);
        int from = page * size;

        if (from >= filtered.size()) {
            return ResponseEntity.ok(new PagedResponse<>(List.of(), page, size, filtered.size()));
        }

        int to = Math.min(from + size, filtered.size());
        List<TaskResponse> content = filtered.subList(from, to)
                .stream()
                .map(TaskResponse::from)
                .toList();

        return ResponseEntity.ok(new PagedResponse<>(content, page, size, filtered.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        return taskService.getById(id)
                .map(task -> ResponseEntity.ok(TaskResponse.from(task)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request
    ) {
        Task updated = new Task();
        updated.setTitle(request.getTitle());
        updated.setDueDate(request.getDueDate());
        updated.setPriority(request.getPriority());
        updated.setCompleted(request.getCompleted() != null && request.getCompleted());

        Task result = taskService.update(id, updated);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(TaskResponse.from(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk-complete")
    public ResponseEntity<String> bulkComplete(@RequestBody List<Long> ids) {
        try {
            taskService.completeMultipleTasks(ids);
            return ResponseEntity.ok("Всі обрані завдання успішно завершені.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}