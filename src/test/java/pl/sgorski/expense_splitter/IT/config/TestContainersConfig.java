package pl.sgorski.expense_splitter.IT.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestContainersConfig {

  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> psqlContainer() {
    //noinspection resource
    return new PostgreSQLContainer<>("postgres:18.2-alpine")
        .withDatabaseName("es_db")
        .withUsername("user")
        .withPassword("password");
  }
}
