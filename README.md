# Spring AI Samples

![Spring Boot AI Samples](https://github.com/susimsek/spring-ai-samples/blob/main/images/introduction.png)

## Overview

Spring AI Samples is a collection of artificial intelligence examples developed using Spring Boot. The goal of this project is to demonstrate how Spring ecosystem design principles such as portability and modular design can be applied to the AI domain, promoting the use of Plain Old Java Objects (POJOs) as the building blocks of AI applications.

## Prerequisites

- Java 17
- Kotlin
- Maven 3.x
- Open AI

## Build

To install dependencies and build the project, run the following command:

```sh
mvn clean install
```

## Testing

To run the application's tests, use the following command:

```sh
mvn verify
```

## Code Quality

To assess code quality locally using SonarQube, execute:

```sh
mvn -Psonar compile initialize sonar:sonar
```

## Detekt

Detekt is a static code analysis tool for the Kotlin programming language. Run Detekt with the following command:

```sh
mvn antrun:run@detekt
```

## Docker

The sample applications can also be fully dockerized. To achieve this, first build a Docker image of your app:

```sh
mvn verify jib:dockerBuild
```

## Used Technologies

- Java 17
- Kotlin
- Docker
- Sonarqube
- Detekt
- Checkstyle
- Spring Boot 3.x
- Spring AI
- Spring Boot Web
- Spring Boot HATEOAS
- Spring Boot Cache
- Spring Boot Validation
- Spring Boot Actuator
- Spring Boot OAuth2 Resource Server
- Spring Boot Security
- Spring Cloud Circuit Breaker Resilience4j
- Spring Boot Aop
- Micrometer tracing
- Micrometer tracing bridge otel
- Loki logback appender
- Commons text
- Internationalization(i18n)
- Lombok
- Mapstruct
- Springdoc