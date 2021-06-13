package se.magnus.microservices.composite.product;

import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.FieldHint;
import org.springframework.nativex.hint.JdkProxyHint;
import org.springframework.nativex.hint.TypeHint;
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
import se.magnus.microservices.composite.product.services.ProductCompositeIntegration;
import se.magnus.util.http.HttpErrorInfo;

@TypeHint(types = Product.class, fields = {
  @FieldHint(name = "productId", allowWrite = true),
  @FieldHint(name = "name", allowWrite = true),
  @FieldHint(name = "weight", allowWrite = true),
  @FieldHint(name = "serviceAddress", allowWrite = true)
})

@TypeHint(types = Recommendation.class, fields = {
  @FieldHint(name = "productId", allowWrite = true),
  @FieldHint(name = "recommendationId", allowWrite = true),
  @FieldHint(name = "author", allowWrite = true),
  @FieldHint(name = "rate", allowWrite = true),
  @FieldHint(name = "content", allowWrite = true),
  @FieldHint(name = "serviceAddress", allowWrite = true)
})

@TypeHint(types = Review.class, fields = {
  @FieldHint(name = "productId", allowWrite = true),
  @FieldHint(name = "reviewId", allowWrite = true),
  @FieldHint(name = "author", allowWrite = true),
  @FieldHint(name = "subject", allowWrite = true),
  @FieldHint(name = "content", allowWrite = true),
  @FieldHint(name = "serviceAddress", allowWrite = true)
})

@TypeHint(types = Product.class, fields = {
  @FieldHint(name = "productId", allowWrite = true),
  @FieldHint(name = "name", allowWrite = true),
  @FieldHint(name = "weight", allowWrite = true),
  @FieldHint(name = "serviceAddress", allowWrite = true)
})

@TypeHint(types = Recommendation.class, fields = {
  @FieldHint(name = "productId", allowWrite = true),
  @FieldHint(name = "recommendationId", allowWrite = true),
  @FieldHint(name = "author", allowWrite = true),
  @FieldHint(name = "rate", allowWrite = true),
  @FieldHint(name = "content", allowWrite = true),
  @FieldHint(name = "serviceAddress", allowWrite = true)
})

@TypeHint(types = Review.class, fields = {
  @FieldHint(name = "productId", allowWrite = true),
  @FieldHint(name = "reviewId", allowWrite = true),
  @FieldHint(name = "author", allowWrite = true),
  @FieldHint(name = "subject", allowWrite = true),
  @FieldHint(name = "content", allowWrite = true),
  @FieldHint(name = "serviceAddress", allowWrite = true)
})

@TypeHint(types = ProductAggregate.class, fields = {
  @FieldHint(name = "productId", allowWrite = true),
  @FieldHint(name = "name", allowWrite = true),
  @FieldHint(name = "weight", allowWrite = true),
  @FieldHint(name = "recommendations", allowWrite = true),
  @FieldHint(name = "reviews", allowWrite = true),
  @FieldHint(name = "serviceAddresses", allowWrite = true)
})

@TypeHint(types = RecommendationSummary.class, fields = {
  @FieldHint(name = "recommendationId", allowWrite = true),
  @FieldHint(name = "author", allowWrite = true),
  @FieldHint(name = "rate", allowWrite = true),
  @FieldHint(name = "content", allowWrite = true)
})

@TypeHint(types = ReviewSummary.class, fields = {
  @FieldHint(name = "reviewId", allowWrite = true),
  @FieldHint(name = "author", allowWrite = true),
  @FieldHint(name = "subject", allowWrite = true),
  @FieldHint(name = "content", allowWrite = true)
})

@TypeHint(types = ServiceAddresses.class, fields = {
  @FieldHint(name = "cmp", allowWrite = true),
  @FieldHint(name = "pro", allowWrite = true),
  @FieldHint(name = "rev", allowWrite = true),
  @FieldHint(name = "rec", allowWrite = true)
})

@TypeHint(types = HttpErrorInfo.class, fields = {
  @FieldHint(name = "timestamp", allowWrite = true),
  @FieldHint(name = "path", allowWrite = true),
  @FieldHint(name = "httpStatus", allowWrite = true),
  @FieldHint(name = "message", allowWrite = true)
})

@TypeHint(types = GenericMessage.class, fields = {
  @FieldHint(name = "headers", allowWrite = true),
  @FieldHint(name = "payload", allowWrite = true),
})

@TypeHint(types = Event.class, fields = {
  @FieldHint(name = "eventType", allowWrite = true),
  @FieldHint(name = "key", allowWrite = true),
  @FieldHint(name = "data", allowWrite = true),
  @FieldHint(name = "eventCreatedAt", allowWrite = true)
})

@TypeHint(types = ZonedDateTimeSerializer.class)

@TypeHint(types = OptionalValidatorFactoryBean.class)

@JdkProxyHint(types = { RestController.class, SynthesizedAnnotation.class })
@JdkProxyHint(types = { Controller.class, SynthesizedAnnotation.class })
@JdkProxyHint(types = { GetMapping.class, SynthesizedAnnotation.class })
@JdkProxyHint(types = { PostMapping.class, SynthesizedAnnotation.class })
@JdkProxyHint(types = { DeleteMapping.class, SynthesizedAnnotation.class })
@JdkProxyHint(types = { RequestParam.class, SynthesizedAnnotation.class })
@JdkProxyHint(types = { RequestHeader.class, SynthesizedAnnotation.class })
@JdkProxyHint(types = { PathVariable.class, SynthesizedAnnotation.class })

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
  @Bean
  public OpenAPI getOpenApiDocumentation() {
    return new OpenAPI()
      .info(new Info().title(apiTitle)
        .description(apiDescription)
        .version(apiVersion)
        .contact(new Contact()
          .name(apiContactName)
          .url(apiContactUrl)
          .email(apiContactEmail))
        .termsOfService(apiTermsOfService)
        .license(new License()
          .name(apiLicense)
          .url(apiLicenseUrl)))
      .externalDocs(new ExternalDocumentation()
        .description(apiExternalDocDesc)
        .url(apiExternalDocUrl));
  }

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
    SpringApplication.run(ProductCompositeServiceApplication.class, args);
  }

}
