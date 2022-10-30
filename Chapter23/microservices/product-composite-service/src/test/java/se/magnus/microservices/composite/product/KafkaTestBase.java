package se.magnus.microservices.composite.product;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class KafkaTestBase {
  private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

  @DynamicPropertySource
  private static void dynamicProperties(DynamicPropertyRegistry registry) {
    kafkaContainer
      .withNetworkAliases("kafka")
      .withExposedPorts(9092, 9093)
      .start();

    registry.add("spring.cloud.stream.kafka.binder.brokers", kafkaContainer::getBootstrapServers);

    // Feed the tracing agent with calls to the expected setter-methods...
    registry.add("spring.cloud.stream.kafka.binder.replicationFactor", () -> Short.valueOf((short)1));
    registry.add("spring.cloud.stream.kafka.binder.defaultBrokerPort", () -> "9092");
  }
}
