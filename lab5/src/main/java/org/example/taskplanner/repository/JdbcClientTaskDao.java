package org.example.taskplanner.repository;

import org.example.taskplanner.model.Task;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class JdbcClientTaskDao implements TaskDao {

    private final JdbcClient jdbcClient;

    public JdbcClientTaskDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Long create(Task task) {
        String sql = "INSERT INTO tasks (title, due_date, priority, completed) VALUES (:title, :dueDate, :priority, :completed)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Виправляємо ланцюжок викликів
        jdbcClient.sql(sql)
                .param("title", task.getTitle())
                .param("dueDate", task.getDueDate())
                .param("priority", task.getPriority())
                .param("completed", task.isCompleted() ? 1 : 0)
                .update(keyHolder, "id"); // Метод повертає int, ключ зберігається в keyHolder

        return keyHolder.getKeyAs(Long.class);
    }

    @Override
    public Optional<Task> read(Long id) {
        return jdbcClient.sql("SELECT id, title, due_date, priority, completed FROM tasks WHERE id = :id")
                .param("id", id)
                .query((rs, rowNum) -> {
                    Task t = new Task();
                    t.setId(rs.getLong("id"));
                    t.setTitle(rs.getString("title"));
                    t.setDueDate(rs.getDate("due_date").toLocalDate());
                    t.setPriority(rs.getString("priority"));
                    t.setCompleted(rs.getInt("completed") == 1);
                    return t;
                })
                .optional();
    }

    @Override
    public void update(Task task) {
        jdbcClient.sql("UPDATE tasks SET title = :title, due_date = :dueDate, priority = :priority, completed = :completed WHERE id = :id")
                .param("title", task.getTitle())
                .param("dueDate", task.getDueDate())
                .param("priority", task.getPriority())
                .param("completed", task.isCompleted() ? 1 : 0)
                .param("id", task.getId())
                .update();
    }

    @Override
    public void delete(Long id) {
        jdbcClient.sql("DELETE FROM tasks WHERE id = :id")
                .param("id", id)
                .update();
    }

    @Override
    public List<Task> findAll() {
        return jdbcClient.sql("SELECT id, title, due_date, priority, completed FROM tasks")
                .query((rs, rowNum) -> {
                    Task t = new Task();
                    t.setId(rs.getLong("id"));
                    t.setTitle(rs.getString("title"));
                    t.setDueDate(rs.getDate("due_date").toLocalDate());
                    t.setPriority(rs.getString("priority"));
                    t.setCompleted(rs.getInt("completed") == 1);
                    return t;
                })
                .list();
    }

    @Override
    public List<Task> findByTitle(String title) {
        return jdbcClient.sql("SELECT id, title, due_date, priority, completed FROM tasks WHERE LOWER(title) LIKE LOWER(:title)")
                .param("title", "%" + title + "%")
                .query((rs, rowNum) -> {
                    Task t = new Task();
                    t.setId(rs.getLong("id"));
                    t.setTitle(rs.getString("title"));
                    t.setDueDate(rs.getDate("due_date").toLocalDate());
                    t.setPriority(rs.getString("priority"));
                    t.setCompleted(rs.getInt("completed") == 1);
                    return t;
                })
                .list();
    }
}