package org.example.taskplanner.controller;

import org.example.taskplanner.model.Task;
import org.example.taskplanner.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", taskService.getAll());
        model.addAttribute("today", LocalDate.now());
        return "index";
    }

    @GetMapping("/new")
    public String newTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "form";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam String title,
                          @RequestParam String dueDate,
                          @RequestParam String priority) {
        Task task = new Task(null, title, LocalDate.parse(dueDate), priority);

        taskService.create(task);

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return "redirect:/";
    }

    @GetMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id) {
        taskService.markCompleted(id);
        return "redirect:/";
    }

    @GetMapping("/sort/priority")
    public String sortByPriority(Model model) {
        model.addAttribute("tasks", taskService.sortByPriority());
        model.addAttribute("today", LocalDate.now());
        return "index";
    }

    @GetMapping("/sort/date")
    public String sortByDate(Model model) {
        model.addAttribute("tasks", taskService.sortByDate());
        model.addAttribute("today", LocalDate.now());
        return "index";
    }
}