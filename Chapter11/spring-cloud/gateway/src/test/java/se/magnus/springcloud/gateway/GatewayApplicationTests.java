package se.magnus.springcloud.gateway;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
  webEnvironment = RANDOM_PORT,
  properties = {
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=some-url",
    "eureka.client.enabled=false"})
class GatewayApplicationTests {

  @Test
  void contextLoads() {
  }

}
