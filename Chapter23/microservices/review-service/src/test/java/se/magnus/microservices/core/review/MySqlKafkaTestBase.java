package se.magnus.microservices.core.review;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class MySqlKafkaTestBase {
  private static MySQLContainer database = new MySQLContainer("mysql:8.0.30");
  private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

  static {
    database.start();

    kafkaContainer
      .withNetworkAliases("kafka")
      .withExposedPorts(9092, 9093)
      .start();
  }

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", database::getJdbcUrl);
    registry.add("spring.datasource.username", database::getUsername);
    registry.add("spring.datasource.password", database::getPassword);

    registry.add("spring.cloud.stream.kafka.binder.brokers", kafkaContainer::getBootstrapServers);

    // Feed the tracing agent with calls to the expected setter-methods...
    registry.add("spring.cloud.stream.kafka.binder.replicationFactor", () -> Short.valueOf((short)1));
    registry.add("spring.cloud.stream.kafka.binder.defaultBrokerPort", () -> "9092");
  }
}
