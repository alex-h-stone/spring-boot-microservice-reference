# Spring Boot Microservice Template

Spring Boot 3 based microservice template integrating features which address a majority of common business requirements.

## Table of Contents

# Table of Contents

- [Introduction and purpose](#introduction-and-purpose)
- [Run the microservice](#run-the-pet-store-microservice)
- [Running Tests](#running-unit-and-integration-tests)
- [Dependency version management](#dependency-version-management)
- [OpenAPI/Swagger Code Generation](#openapiswagger-code-generation)
- [Data Persistence via MongoDB](#data-persistence-via-mongodb)
- [Exception Handling](#exception-handling)
- [Java Request Validation](#java-request-validation)
- [Request Validation via OpenAPI Specification](#request-validation-via-openapi-specification)
- [API to DTO Mapping](#api-to-dto-mapping)
- [Unit Tests](#unit-tests)
- [Integration Tests](#integration-tests)
- [Metrics Endpoint](#metrics-endpoint)
- [External REST API Call with Retry via Spring WebFlux](#external-rest-api-call-with-retry-via-spring-webflux)
- [Stubbing of External API Calls via WireMock](#stubbing-of-external-api-calls-via-wiremock)
- [Logging of Requests and Responses](#logging-of-requests-and-responses)
- [Actuator Endpoints](#actuator-endpoints)
- [Swagger Documentation Endpoints](#swagger-documentation-endpoints)

---

#### Introduction and purpose

TODO Alex

The API is defined by the OpenAPI specification `pet-store-api.yaml` and can be viewed in the Swagger
Editor https://editor.swagger.io/

The microservice is structured with Controller and Service layers.
Depending on the use case it may be desirable to also include a mapping layer to translate between one or more of the
following:
TBC

- API model types (pet store)
- External API call - API model types (vaccinations)
- Datastore/Repository entity types (MongoDB)

The API provides the following functionality: TBC

- New pets can be added to the pet store.
- Customers can search for a pet.
- Pets can be updated when details change.
- Customers can purchase a pet.
- Pet vaccination details are provided via an external API call.

---

#### Run the Pet Store microservice
Requirements to run locally:

* Java 21 e.g. https://jdk.java.net/21/
* MongoDB or equivalent (e.g. Microsoft Azure CosmosDB via [CGI
  Sandbox](https://ensemble.ent.cgi.com/business/305832/serviceexcellence/Service%20Excellence%20Wiki%20Library/Sandbox.aspx))
* An IDE, IntelliJ (recommended), or Eclipse

When the above have been satisfied, to start the microservice:

1. Start the local stub server on a port via:  
   `./gradlew startVaccinationsWireMockServer -PportNumber=8081`
2. Update the environment variables defined in `cleanBuildTestAndRun.ps1` with your MongoDB URI `MONGO_DB_URI` and stub
   server URL `VACCINATIONS_URL` with port number matching the above WireMockServer port.
3. Start the microservice via:  
   `./cleanBuildTestAndRun.ps1`

---

#### TODO Alex TODO

- Add extra unit tests
- Flesh out the description, purpose and role of this project
- Include alternative frameworks and options
- Postman Collection integrated via a CI tool
- Reduce duplication in var/path names e.g. 'external'
- Add WebSecurity OAuth2?
- Add tracing in logging
- Add code style plugin support via https://github.com/diffplug/spotless
  or https://plugins.gradle.org/plugin/org.ec4j.editorconfig

---

#### Running unit and integration tests

Integration tests are identified by the JUnit annotation `@Tag("integration")` which is present on `BaseIntegrationTest`
so is inherited by all integration test classes which extend `BaseIntegrationTest`.

By default, all tests (unit and integration) are run:  
`./gradlew test`

To run only integration tests:  
`./gradlew -PincludeTag=integration test`

To run only unit tests  
`./gradlew -PexcludeTag=integration test`

---

#### Dependency version management

To best manage a large number of Spring dependencies with independent version numbers this template uses the
Gradle plugins:

- `org.springframework.boot`
- `io.spring.dependency-management`

In addition to the dependency management BOM's:

- `org.springframework.boot:spring-boot-dependencies`
- `org.junit.jupiter:junit-jupiter`

With all Gradle dependency and plugin version numbers defined in one place in `gradle.properties  `

---

#### OpenAPI/Swagger code generation

See the `build.gradle` for an example of using OpenAPI schemas (`pet-store-api.yaml` and `animal-vaccination-api.yaml`)
to generate model classes and the Java interfaces for the APIs.  
By having a controller implement the Java interface derived from the API, when the schema is updated some
breaking changes will be caught as compile time errors.

To generate all OpenAPI Java classes and interfaces:  
`./gradlew generateAllOpenAPI`

To generate the Pet Store API classes and interfaces:  
`./gradlew generatePetStoreClasses`

To generate the external Animal Vaccination API classes and interfaces:  
`./gradlew generateExternalAnimalVaccinationClasses`

---

#### Data persistence via MongoDB

This service is integrated with a MongoDB NoSQL database using `spring-boot-starter-data-mongodb`
and the `MongoRepository` interface. Connection details are defined in the `application.yaml` under
`spring.data.mongodb`

For a more lightweight and simpler MongoDB integration consider using
the [mongodb-driver-sync](https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-sync/4.11.1)
client library. Although the out-of-the-box features of `MongoRepository` and `@Document` will not be available.


---

#### Exception handling

See the `GlobalExceptionHandler` class for details on how to implement exception handling, along with
the application exception class `AbstractApplicationException`

---

#### Java request validation

See `PetValidator` and the integration test
`shouldReturnErrorWhenCallingGetPetEndpointWithInvalidIdFailingValidation`  
for details of how to write custom request validation logic.

---

#### Request validation via OpenAPI specification

See `/pets/{petId}` GET endpoint and the `petId schema` definition in `pet-store-api.yaml`
and the associated integration
test `shouldReturnErrorWhenCallingGetPetEndpointWithIdLargerThanPermitted` for details of how request validation can
be implemented via the API yaml.

---

#### API to DTO mapping

See the mappers `PetMapper`, `ExternalVaccinationsMapper` and `CustomerMapper` for examples of how to define logic to
map between API, DTO and Mongo DB Document objects.  
Depending on the use case [MapStruct](https://mapstruct.org/) is an option for reducing boilerplate mapping code.

---

#### Unit tests

See JUnit tests in java which do not have the annotation `@Tag("integration")` or extend `BaseIntegrationTest`
The same JUnit tag allows us to execute integration tests and unit tests separately if needed.

---

#### Integration tests

For examples of integration tests utilising WireMock and `de.flapdoodle` embedded MongoDB see any JUnit test which
either
extends `BaseIntegrationTest` or has the JUnit annotation `@Tag("integration")`.

---

#### Metrics endpoint
See the metrics endpoint provided by Spring Actuator https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints
- GET http://localhost:8099/actuator/metrics

---

#### External REST API call with retry via Spring WebFlux

See `VaccinationsApiClient` for an example of making an external REST API call with retry logic
using the SpringFlux `WebClient`.

---

#### Stubbing of external API calls via WireMock

See https://wiremock.org/docs/stubbing/ for additional guidance with WireMock.

Also see `VaccinationsWireMockServer` for how to define a stub server for running a microservice locally with external
API dependencies.

In addition, `WiremockServerForIntegrationTests` for how to utilise WireMock for automated integration tests.

---

#### Logging of requests and responses

See `RequestLoggingFilterConfig` for the required config to log requests.

Also consider the use of `LoggingAspects` and the `LogMethodArguments` annotation to log method arguments,
and the `LogMethodResponse` to log method responses.

---

#### Actuator endpoints

Determined by dependencies ("OpenAPI/Swagger docs" in `build.gradle`),
application config (`application.yaml`) and security config (`SecurityConfig`)

https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints

- GET http://localhost:8099/actuator
- GET http://localhost:8099/actuator/prometheus
- GET http://localhost:8099/actuator/health
- GET http://localhost:8099/actuator/info
- GET http://localhost:8099/actuator/metrics
- GET http://localhost:8099/actuator/mappings

Where 8099 is the Management Server Port `management.server.port`

---

#### Swagger documentation endpoints

OpenAPI endpoints to provide live up-to-date API documentation.

- http://localhost:8080/v3/api-docs
- http://localhost:8080/v3/api-docs.yaml
- http://localhost:8080/v3/api-docs/springdoc
- http://localhost:8080/v3/api-docs/swagger-config

Where 8080 is the Application Server Port `server.port`
