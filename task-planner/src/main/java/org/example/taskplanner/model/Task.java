package org.example.taskplanner.model;

import java.time.LocalDate;

public class Task {
    private Long id;
    private String title;
    private LocalDate dueDate;
    private String priority; // Low, Medium, High
    private boolean completed;

    public Task() {}

    public Task(Long id, String title, LocalDate dueDate, String priority) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
