package com.example.transactiontimeoutsamplejdbc;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.test.context.jdbc.Sql;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;

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
    // PostgreSQLの場合
    @Test
    @DisplayName("DataAccessResourceFailureException in 2 seconds on PostgreSQL")
    void postgres() {
      long startTime = System.currentTimeMillis();
      DataAccessResourceFailureException exception = assertThrows(
          DataAccessResourceFailureException.class, () -> {
            sampleService.registerWithSleep(new Sample(4, "Sample4"), 3);  // timeout = 2
          }
      );
      long processSeconds = (System.currentTimeMillis() - startTime) / 1000;
      assertEquals(2, processSeconds);  // timeout should be 2 seconds
      // PostgreSQL's code that means query_canceled
      // see https://www.postgresql.org/docs/16/errcodes-appendix.html
      SQLException sqlException = (SQLException) exception.getCause();
      assertEquals("57014", (sqlException).getSQLState());
      assertEquals("org.postgresql.util.PSQLException", sqlException.getClass().getName());
    }

    // MySQLの場合
    @Disabled
    @Test
    @DisplayName("QueryTimeoutException in 2 seconds on MySQL")
    void mysql() {
      long startTime = System.currentTimeMillis();
      QueryTimeoutException exception = assertThrows(
          QueryTimeoutException.class, () -> {
            sampleService.registerWithSleep(new Sample(4, "Sample4"), 3);  // timeout = 2
          }
      );
      long processSeconds = (System.currentTimeMillis() - startTime) / 1000;
      assertEquals(2, processSeconds);  // timeout should be 2 seconds
      SQLTimeoutException sqlException = (SQLTimeoutException) exception.getCause();
      assertEquals(0, sqlException.getErrorCode());
      assertNull(sqlException.getSQLState());
    }
  }
}
