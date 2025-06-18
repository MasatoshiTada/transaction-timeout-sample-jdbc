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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
    scripts = {"classpath:schema.sql", "classpath:data.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class SampleServiceTest {
  @Autowired
  SampleService sampleService;

  @Nested
  @DisplayName("registerWithSleep()")
  class RegisterWithSleepTest {
    @Test
    @DisplayName("TransactionTimedOutException in 2 seconds")
    void sleep3() {
      long startTime = System.currentTimeMillis();
      DataAccessResourceFailureException exception = assertThrows(
          DataAccessResourceFailureException.class, () -> {
            sampleService.registerWithSleep(new Sample(4, "Sample4"), 3);  // timeout = 2
          }
      );
      // PostgreSQL's code that means query_canceled
      // see https://www.postgresql.org/docs/16/errcodes-appendix.html
      assertEquals("57014", ((SQLException) exception.getCause()).getSQLState());
      long processSeconds = (System.currentTimeMillis() - startTime) / 1000;
      assertEquals(2, processSeconds);  // timeout should be 2 seconds
    }
  }
}
