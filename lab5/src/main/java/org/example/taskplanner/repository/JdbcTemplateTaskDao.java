package org.example.taskplanner.repository;

import org.example.taskplanner.model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTemplateTaskDao implements TaskDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateTaskDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDueDate(rs.getDate("due_date").toLocalDate());
        task.setPriority(rs.getString("priority"));
        task.setCompleted(rs.getInt("completed") == 1);
        return task;
    };

    @Override
    public Long create(Task task) {
        String sql = "INSERT INTO tasks (title, due_date, priority, completed) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, task.getTitle());
            ps.setDate(2, java.sql.Date.valueOf(task.getDueDate()));
            ps.setString(3, task.getPriority());
            ps.setInt(4, task.isCompleted() ? 1 : 0);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Task> read(Long id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        List<Task> tasks = jdbcTemplate.query(sql, taskRowMapper, id);
        return tasks.stream().findFirst();
    }

    @Override
    public void update(Task task) {
        String sql = "UPDATE tasks SET title = ?, due_date = ?, priority = ?, completed = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                task.getTitle(),
                java.sql.Date.valueOf(task.getDueDate()),
                task.getPriority(),
                task.isCompleted() ? 1 : 0,
                task.getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Task> findAll() {
        String sql = "SELECT * FROM tasks";
        return jdbcTemplate.query(sql, taskRowMapper);
    }

    @Override
    public List<Task> findByTitle(String title) {
        String sql = "SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, taskRowMapper, "%" + title + "%");
    }
}