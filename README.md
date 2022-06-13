# Microservices with Spring Boot and Spring Cloud, Second Edition 

<a href="https://www.packtpub.com/product/microservices-with-spring-boot-and-spring-cloud-second-edition/9781801072977"><img src="https://static.packt-cdn.com/products/9781801072977/cover/normal" alt="Microservices with Spring Boot and Spring Cloud, Second Edition" height="256px" align="right"></a>

This is the code repository for [Microservices with Spring Boot and Spring Cloud, Second Edition](https://www.packtpub.com/product/microservices-with-spring-boot-and-spring-cloud-second-edition/9781801072977), published by Packt.

**Build resilient and scalable microservices using Spring Cloud, Istio, and Kubernetes**
## About the book

With this book, you'll learn how to efficiently build and deploy microservices. This new edition has been updated for the most recent versions of Spring, Java, Kubernetes, and Istio, demonstrating faster and simpler handling of Spring Boot, local Kubernetes clusters, and Istio installation. The expanded scope includes native compilation of Spring-based microservices, support for Mac and Windows with WSL2, and an introduction to Helm 3 for packaging and deployment. A revamped security chapter now follows the OAuth 2.1 specification and makes use of the newly launched Spring Authorization Server from the Spring team.

Starting with a set of simple cooperating microservices, you'll add persistence and resilience, make your microservices reactive, and document their APIs using OpenAPI.

You’ll understand how fundamental design patterns are applied to add important functionality, such as service discovery with Netflix Eureka and edge servers with Spring Cloud Gateway. You’ll learn how to deploy your microservices using Kubernetes and adopt Istio. You'll explore centralized log management using the Elasticsearch, Fluentd, and Kibana (EFK) stack and monitor microservices using Prometheus and Grafana.

By the end of this book, you'll be confident in building microservices that are scalable and robust using Spring Boot and Spring Cloud.

## What you will learn
1. Build reactive microservices using Spring Boot
2. Develop resilient and scalable microservices using Spring Cloud
3. Use OAuth 2.1/OIDC and Spring Security to protect public APIs
4. Implement Docker to bridge the gap between development, testing, and production
5. Deploy and manage microservices with Kubernetes
6. Apply Istio for improved security, observability, and traffic management
7. Write and run automated microservice tests with JUnit, testcontainers, Gradle, and bash

## Errata
* **Page 147 (Defining index for MongoDB):** The description does not mention that auto-creation of an index is disabled since Spring Data MongoDB 3.0. Therefore, index creation is handled programmatically by the main classes `ProductServiceApplication` and `RecommendationServiceApplication`.
For example, see the method [initIndicesAfterStartup](https://github.com/PacktPublishing/Hands-On-Microservices-with-Spring-Boot-and-Spring-Cloud/blob/191f93f56f0d58eae4227a1952c73b4b10e8bac0/Chapter06/microservices/product-service/src/main/java/se/magnus/microservices/core/product/ProductServiceApplication.java#L39-L47) in the `ProductServiceApplication` class.

  For more information, see:
  * [What’s New in Spring Data MongoDB 3.0](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#new-features.3.0)
  * [Spring Data MongoDB - Index Creation](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mapping.index-creation)

* **Page 153 (Testcontainers for MongoDB):** *The sentence* **For the `product` and `review` microservices, which use MongoDB, a corresponding base class, `MongoDbTestBase`, has been added** *should be* **For the `product` and `recommendation` microservices, which use MongoDB, a corresponding base class, `MongoDbTestBase`, has been added**.
