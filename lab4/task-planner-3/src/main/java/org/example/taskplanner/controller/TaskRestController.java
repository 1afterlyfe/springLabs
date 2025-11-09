package org.example.taskplanner.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.example.taskplanner.dto.PagedResponse;
import org.example.taskplanner.dto.TaskRequest;
import org.example.taskplanner.dto.TaskResponse;
import org.example.taskplanner.model.Task;
import org.example.taskplanner.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {

    private final TaskService taskService;
    private final ObjectMapper objectMapper;

    public TaskRestController(TaskService taskService, ObjectMapper objectMapper) {
        this.taskService = taskService;
        this.objectMapper = objectMapper;
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
                .body(TaskResponse.from(created)); // 201
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
            return ResponseEntity.badRequest().build(); // 400
        }

        List<Task> filtered = taskService.filter(title, priority, completed);
        int from = page * size;
        if (from >= filtered.size()) {
            return ResponseEntity.ok(
                    new PagedResponse<>(List.of(), page, size, filtered.size())
            );
        }

        int to = Math.min(from + size, filtered.size());
        List<TaskResponse> content = filtered.subList(from, to)
                .stream()
                .map(TaskResponse::from)
                .toList();

        PagedResponse<TaskResponse> response =
                new PagedResponse<>(content, page, size, filtered.size());
        return ResponseEntity.ok(response); // 200
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        return taskService.getById(id)
                .map(task -> ResponseEntity.ok(TaskResponse.from(task))) // 200
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // 404
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
        return ResponseEntity.ok(TaskResponse.from(result)); // 200
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
        return ResponseEntity.noContent().build(); // 204
    }

    // --- PARTIAL UPDATE: JSON Merge Patch (RFC 7386) ---
    @PatchMapping(
            value = "/{id}",
            consumes = "application/merge-patch+json",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TaskResponse> mergePatchTask(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates
    ) {
        return taskService.getById(id)
                .map(existing -> {
                    if (updates.containsKey("title")) {
                        existing.setTitle((String) updates.get("title"));
                    }
                    if (updates.containsKey("priority")) {
                        existing.setPriority((String) updates.get("priority"));
                    }
                    if (updates.containsKey("completed")) {
                        existing.setCompleted(Boolean.TRUE.equals(updates.get("completed")));
                    }
                    if (updates.containsKey("dueDate")) {
                        String dateStr = (String) updates.get("dueDate");
                        existing.setDueDate(dateStr != null ? java.time.LocalDate.parse(dateStr) : null);
                    }
                    Task saved = taskService.create(existing);
                    return ResponseEntity.ok(TaskResponse.from(saved)); // 200
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // --- PARTIAL UPDATE: JSON Patch (RFC 6902) ---
    @PatchMapping(
            value = "/{id}/json-patch",
            consumes = "application/json-patch+json",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TaskResponse> jsonPatchTask(
            @PathVariable Long id,
            @RequestBody JsonPatch patch
    ) {
        return taskService.getById(id)
                .map(existing -> {
                    try {
                        Task patched = applyPatchToTask(patch, existing);
                        Task saved = taskService.create(patched);
                        return ResponseEntity.ok(TaskResponse.from(saved)); // 200
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).<TaskResponse>build(); // 400
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<TaskResponse>build()); // 404
    }

    private Task applyPatchToTask(JsonPatch patch, Task targetTask) throws Exception {
        JsonNode target = objectMapper.valueToTree(targetTask);
        JsonNode patched = patch.apply(target);
        return objectMapper.treeToValue(patched, Task.class);
    }
}
