package com.example.transactiontimeoutsamplejdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
    scripts = {"classpath:schema.sql", "classpath:data.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class SampleRepositoryTest {
  @Autowired
  SampleRepository sampleRepository;

  @Nested
  @DisplayName("selectAll()")
  class SelectAllTest {
    @Test
    @DisplayName("Can select 3 samples")
    void testSelectAll() {
      List<Sample> samples = sampleRepository.selectAll();
      assertEquals(
          List.of(
              new Sample(1, "Sample1"),
              new Sample(2, "Sample2"),
              new Sample(3, "Sample3")
          ), samples
      );
    }
  }

  @Nested
  @DisplayName("insert()")
  class InsertTest {
    @Test
    @DisplayName("Can insert a new sample")
    void testInsert() {
      Sample sample = new Sample(4, "Sample4");
      sampleRepository.insert(sample);
      assertEquals(
          List.of(
              new Sample(1, "Sample1"),
              new Sample(2, "Sample2"),
              new Sample(3, "Sample3"),
              new Sample(4, "Sample4")
          ), sampleRepository.selectAll()
      );
    }
  }

  @Nested
  @DisplayName("sleep()")
  class SleepTest {
    @Test
    @DisplayName("Can sleep for specified seconds")
    void testSleep() {
      long startTime = System.currentTimeMillis();
      sampleRepository.sleep(2);
      long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
      assertEquals(2, elapsedSeconds);
    }
  }
}
