package org.example.taskplanner.service;

public class SimpleLogger {
    private String name;

    public SimpleLogger(String name) {
        this.name = name;
    }

    public void log(String msg) {
        System.out.println("[" + name + "] " + msg);
    }
}
