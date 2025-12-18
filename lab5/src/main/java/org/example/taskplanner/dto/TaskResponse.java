package org.example.taskplanner.dto;

import org.example.taskplanner.model.Task;

import java.time.LocalDate;

public class TaskResponse {

    private Long id;
    private String title;
    private LocalDate dueDate;
    private String priority;
    private boolean completed;

    public static TaskResponse from(Task task) {
        TaskResponse r = new TaskResponse();
        r.id = task.getId();
        r.title = task.getTitle();
        r.dueDate = task.getDueDate();
        r.priority = task.getPriority();
        r.completed = task.isCompleted();
        return r;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }
}
