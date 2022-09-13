package se.magnus.microservices.core.recommendation;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class MongoDbKafkaTestBase {
  private static final MongoDBContainer database = new MongoDBContainer("mongo:4.4.2");
  private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

  static {
    database.start();

    kafkaContainer
      .withNetworkAliases("kafka")
      .withExposedPorts(9092, 9093)
      .start();
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.host", database::getContainerIpAddress);
    registry.add("spring.data.mongodb.port", () -> database.getMappedPort(27017));
    registry.add("spring.data.mongodb.database", () -> "test");

    registry.add("spring.cloud.stream.kafka.binder.brokers", kafkaContainer::getBootstrapServers);

    // Feed the tracing agent with calls to the expected setter-methods...
    registry.add("spring.cloud.stream.kafka.binder.replicationFactor", () -> Short.valueOf((short)1));
    registry.add("spring.cloud.stream.kafka.binder.defaultBrokerPort", () -> "9092");
  }
}
