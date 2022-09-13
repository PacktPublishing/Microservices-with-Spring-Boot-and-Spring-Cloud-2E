package se.magnus.microservices.composite.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.review.Review;
import se.magnus.api.event.Event;

@Configuration
public class MessageTestConsumerConfig {

  private static final Logger LOG = LoggerFactory.getLogger(MessageTestConsumerConfig.class);

  private ObjectMapper mapper = new ObjectMapper();

  private List<String> productMessages = new ArrayList<>();
  private List<String> recommendationMessages = new ArrayList<>();
  private List<String> reviewMessages = new ArrayList<>();

  public List<String> getAndRemoveProductMessages() {
    List<String> messages = productMessages;
    productMessages = new ArrayList();
    return messages;
  }

  public List<String> getAndRemoveRecommendationMessages() {
    List<String> messages = recommendationMessages;
    recommendationMessages = new ArrayList();
    return messages;
  }

  public List<String> getAndRemoveReviewMessages() {
    List<String> messages = reviewMessages;
    reviewMessages = new ArrayList();
    return messages;
  }

  @Bean
  public Consumer<Event<Integer, Product>> productMessageProcessor() {
    return event -> {
      LOG.debug("Consume Product message of type {}, created at {}...", event.getEventType(), event.getEventCreatedAt());
      productMessages.add(convertObjectToJsonString(event));
    };
  }

  @Bean
  public Consumer<Event<Integer, Recommendation>> recommendationMessageProcessor() {
    return event -> {
      LOG.debug("Consume Recommendation message of type {}, created at {}...", event.getEventType(), event.getEventCreatedAt());
      recommendationMessages.add(convertObjectToJsonString(event));
    };
  }

  @Bean
  public Consumer<Event<Integer, Review>> reviewMessageProcessor() {
    return event -> {
      LOG.debug("Consume Review message of type {}, created at {}...", event.getEventType(), event.getEventCreatedAt());
      reviewMessages.add(convertObjectToJsonString(event));
    };
  }

  private String convertObjectToJsonString(Object object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
