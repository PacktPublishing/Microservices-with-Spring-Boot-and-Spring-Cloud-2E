package se.magnus.springcloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class GatewayApplication {

  @Bean
  @LoadBalanced
  public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }

  @Bean
  public WebClient webClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder.build();
  }

  public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }

}
