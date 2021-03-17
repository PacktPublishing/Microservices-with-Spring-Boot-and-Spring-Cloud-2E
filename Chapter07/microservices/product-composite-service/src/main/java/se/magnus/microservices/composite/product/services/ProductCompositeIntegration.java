package se.magnus.microservices.composite.product.services;

import static reactor.core.publisher.Flux.empty;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.api.event.Event;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.api.exceptions.NotFoundException;
import se.magnus.util.http.HttpErrorInfo;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

  private final WebClient webClient;
  private final ObjectMapper mapper;

  private final String productServiceUrl;
  private final String recommendationServiceUrl;
  private final String reviewServiceUrl;

  private final StreamBridge streamBridge;

  @Autowired
  public ProductCompositeIntegration(
    WebClient.Builder webClient,
    ObjectMapper mapper,
    StreamBridge streamBridge,

    @Value("${app.product-service.host}") String productServiceHost,
    @Value("${app.product-service.port}") int  productServicePort,

    @Value("${app.recommendation-service.host}") String recommendationServiceHost,
    @Value("${app.recommendation-service.port}") int  recommendationServicePort,

    @Value("${app.review-service.host}") String reviewServiceHost,
    @Value("${app.review-service.port}") int  reviewServicePort
  ) {

    this.webClient = webClient.build();
    this.mapper = mapper;
    this.streamBridge = streamBridge;

    productServiceUrl        = "http://" + productServiceHost + ":" + productServicePort;
    recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort;
    reviewServiceUrl         = "http://" + reviewServiceHost + ":" + reviewServicePort;
  }

  @Override
  public Mono<Product> createProduct(Product body) {
    sendMessage("products-out-0", new Event(CREATE, body.getProductId(), body));
    return Mono.just(body);
  }

  @Override
  public Mono<Product> getProduct(int productId) {
    String url = productServiceUrl + "/product/" + productId;
    LOG.debug("Will call the getProduct API on URL: {}", url);

    return webClient.get().uri(url).retrieve().bodyToMono(Product.class).log().onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
  }

  @Override
  public Mono<Void> deleteProduct(int productId) {
    sendMessage("products-out-0", new Event(DELETE, productId, null));
    return Mono.empty();
  }

  @Override
  public Mono<Recommendation> createRecommendation(Recommendation body) {
    sendMessage("recommendations-out-0", new Event(CREATE, body.getProductId(), body));
    return Mono.just(body);
  }

  @Override
  public Flux<Recommendation> getRecommendations(int productId) {

    String url = recommendationServiceUrl + "/recommendation?productId=" + productId;

    LOG.debug("Will call the getRecommendations API on URL: {}", url);

    // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
    return webClient.get().uri(url).retrieve().bodyToFlux(Recommendation.class).log().onErrorResume(error -> empty());
  }

  @Override
  public Mono<Void> deleteRecommendations(int productId) {
    sendMessage("recommendations-out-0", new Event(DELETE, productId, null));
    return Mono.empty();
  }

  @Override
  public Mono<Review> createReview(Review body) {
    sendMessage("reviews-out-0", new Event(CREATE, body.getProductId(), body));
    return Mono.just(body);
  }

  @Override
  public Flux<Review> getReviews(int productId) {

    String url = reviewServiceUrl + "/review?productId=" + productId;

    LOG.debug("Will call the getReviews API on URL: {}", url);

    // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
    return webClient.get().uri(url).retrieve().bodyToFlux(Review.class).log().onErrorResume(error -> empty());

  }

  @Override
  public Mono<Void> deleteReviews(int productId) {
    sendMessage("reviews-out-0", new Event(DELETE, productId, null));
    return Mono.empty();
  }

  public Mono<Health> getProductHealth() {
    return getHealth(productServiceUrl);
  }

  public Mono<Health> getRecommendationHealth() {
    return getHealth(recommendationServiceUrl);
  }

  public Mono<Health> getReviewHealth() {
    return getHealth(reviewServiceUrl);
  }

  private Mono<Health> getHealth(String url) {
    url += "/actuator/health";
    LOG.debug("Will call the Health API on URL: {}", url);
    return webClient.get().uri(url).retrieve().bodyToMono(String.class)
      .map(s -> new Health.Builder().up().build())
      .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
      .log();
  }

  private void sendMessage(String bindingName, Event event) {
    Message message = MessageBuilder.withPayload(event)
      .setHeader("partitionKey", event.getKey())
      .build();
    streamBridge.send(bindingName, message);
  }

  private Throwable handleException(Throwable ex) {

    if (!(ex instanceof WebClientResponseException)) {
      LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
      return ex;
    }

    WebClientResponseException wcre = (WebClientResponseException)ex;

    switch (wcre.getStatusCode()) {

      case NOT_FOUND:
        return new NotFoundException(getErrorMessage(wcre));

      case UNPROCESSABLE_ENTITY :
        return new InvalidInputException(getErrorMessage(wcre));

      default:
        LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
        LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
        return ex;
    }
  }

  private String getErrorMessage(WebClientResponseException ex) {
    try {
      return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    } catch (IOException ioex) {
      return ex.getMessage();
    }
  }
}