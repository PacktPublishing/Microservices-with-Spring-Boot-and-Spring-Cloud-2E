package se.magnus.microservices.composite.product;

import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
//import io.swagger.v3.oas.models.ExternalDocumentation;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Contact;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.DecoratingProxy;
import org.springframework.core.annotation.SynthesizedAnnotation;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.nativex.hint.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import se.magnus.api.composite.product.ProductAggregate;
import se.magnus.api.composite.product.RecommendationSummary;
import se.magnus.api.composite.product.ReviewSummary;
import se.magnus.api.composite.product.ServiceAddresses;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.api.event.Event;
import se.magnus.api.exceptions.BadRequestException;
import se.magnus.api.exceptions.EventProcessingException;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.api.exceptions.NotFoundException;
import se.magnus.microservices.composite.product.services.ProductCompositeIntegration;
import se.magnus.util.http.HttpErrorInfo;

@TypeHint(types = {
  Product.class,
  Recommendation.class,
  Review.class,
  ProductAggregate.class,
  RecommendationSummary.class,
  ReviewSummary.class,
  ServiceAddresses.class,
  HttpErrorInfo.class,
  BadRequestException.class,
  EventProcessingException.class,
  InvalidInputException.class,
  NotFoundException.class,
  GenericMessage.class,
  Event.class,
  ZonedDateTimeSerializer.class,
  OptionalValidatorFactoryBean.class},
  access = { TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS }
)

@TypeHint(typeNames = "org.springframework.cloud.stream.binder.kafka.config.KafkaBinderConfiguration$KafkaBinderMetricsConfiguration")

@JdkProxyHint(types = {
  RestController.class,
  Controller.class,
  GetMapping.class,
  PostMapping.class,
  DeleteMapping.class,
  RequestParam.class,
  RequestHeader.class,
  PathVariable.class,
  SynthesizedAnnotation.class
})

@AotProxyHint(targetClass = ProductCompositeIntegration.class, interfaces = {
  ProductService.class,
  RecommendationService.class,
  ReviewService.class,
  SpringProxy.class,
  Advised.class,
  DecoratingProxy.class
})

@SpringBootApplication
@ComponentScan("se.magnus")
public class ProductCompositeServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceApplication.class);

  @Value("${api.common.version}")         String apiVersion;
  @Value("${api.common.title}")           String apiTitle;
  @Value("${api.common.description}")     String apiDescription;
  @Value("${api.common.termsOfService}")  String apiTermsOfService;
  @Value("${api.common.license}")         String apiLicense;
  @Value("${api.common.licenseUrl}")      String apiLicenseUrl;
  @Value("${api.common.externalDocDesc}") String apiExternalDocDesc;
  @Value("${api.common.externalDocUrl}")  String apiExternalDocUrl;
  @Value("${api.common.contact.name}")    String apiContactName;
  @Value("${api.common.contact.url}")     String apiContactUrl;
  @Value("${api.common.contact.email}")   String apiContactEmail;

  /**
  * Will exposed on $HOST:$PORT/swagger-ui.html
  *
  * @return the common OpenAPI documentation
  */
  //  @Bean
  //  public OpenAPI getOpenApiDocumentation() {
  //    return new OpenAPI()
  //      .info(new Info().title(apiTitle)
  //        .description(apiDescription)
  //        .version(apiVersion)
  //        .contact(new Contact()
  //          .name(apiContactName)
  //          .url(apiContactUrl)
  //          .email(apiContactEmail))
  //        .termsOfService(apiTermsOfService)
  //        .license(new License()
  //          .name(apiLicense)
  //          .url(apiLicenseUrl)))
  //      .externalDocs(new ExternalDocumentation()
  //        .description(apiExternalDocDesc)
  //        .url(apiExternalDocUrl));
  //  }

  private final Integer threadPoolSize;
  private final Integer taskQueueSize;

  @Autowired
  public ProductCompositeServiceApplication(
    @Value("${app.threadPoolSize:10}") Integer threadPoolSize,
    @Value("${app.taskQueueSize:100}") Integer taskQueueSize
  ) {
    this.threadPoolSize = threadPoolSize;
    this.taskQueueSize = taskQueueSize;
  }

  @Bean
  public Scheduler publishEventScheduler() {
    LOG.info("Creates a messagingScheduler with connectionPoolSize = {}", threadPoolSize);
    return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "publish-pool");
  }

  @Bean
  public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }

  public static void main(String[] args) {
    LOG.info("Starts up with support for Resilience4j and OpenApi disabled!");
    SpringApplication.run(ProductCompositeServiceApplication.class, args);
  }

}
