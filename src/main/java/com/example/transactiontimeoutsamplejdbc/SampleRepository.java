package com.example.transactiontimeoutsamplejdbc;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SampleRepository {
  private final JdbcTemplate jdbcTemplate;

  public SampleRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Sample> selectAll() {
    return jdbcTemplate.query("SELECT id, name FROM sample ORDER BY id", new DataClassRowMapper<>(Sample.class));
  }

  public Object sleep(int seconds) {
    // PostgreSQL
    return jdbcTemplate.queryForObject("SELECT pg_sleep(?)", new Object[]{seconds}, Object.class);
    // MySQL
//    return jdbcTemplate.queryForObject("SELECT sleep(?)", new Object[]{seconds}, Object.class);
  }

  public void insert(Sample sample) {
    jdbcTemplate.update("INSERT INTO sample(id, name) VALUES (?, ?)", sample.id(), sample.name());
  }
}
