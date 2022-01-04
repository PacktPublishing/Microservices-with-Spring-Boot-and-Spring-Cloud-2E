package se.magnus.microservices.core.review;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class MySqlTestBase {
  private static MySQLContainer database = new MySQLContainer(DockerImageName.parse("mariadb:10.7.1").asCompatibleSubstituteFor("mysql"));

  static {
    database.start();
  }

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", database::getJdbcUrl);
    registry.add("spring.datasource.username", database::getUsername);
    registry.add("spring.datasource.password", database::getPassword);
  }

}
