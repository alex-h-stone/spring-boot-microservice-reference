# Spring Boot Microservice Template

Spring Boot 3 based microservice template integrating features which address a majority of common business requirements.

## Table of Contents

- [OpenAPI contract driven development](#openapi-contract-driven-development)
- [Data persistence via MongoDB](#data-persistence-via-mongodb)
- [Exception handling](#exception-handling)
- [Custom request validation](#custom-request-validation)
- [Request validation via OpenAPI specification](#request-validation-via-openapi-specification)
- [Custom Enum serialisation/deserialisation logic](#custom-enum-serialisationdeserialisation-logic)
- [Unit tests](#unit-tests)
- [Integration tests](#integration-tests)
- [Metrics endpoint](#metrics-endpoint)
- [External REST API calls via Spring WebFlux](#external-rest-api-calls-via-spring-webflux)
- [Stubbing of external API calls via WireMock](#stubbing-of-external-api-calls-via-wiremock)
- [Logging of requests and responses](#logging-of-requests-and-responses)
- [Failure recovery via Spring Retry](#failure-recovery-via-spring-retry)
- [Actuator endpoints](#actuator-endpoints)
- [Swagger documentation endpoints](#swagger-documentation-endpoints)

Requirements to run locally:

* Java 21 e.g. https://jdk.java.net/21/
* MongoDB or equivalent (e.g. Microsoft Azure CosmosDB via [CGI
  Sandbox](https://ensemble.ent.cgi.com/business/305832/serviceexcellence/Service%20Excellence%20Wiki%20Library/Sandbox.aspx))
* An IDE, IntelliJ (recommended), or Eclipse

When the above have been satisfied, to start the microservice:

1. Start the local stub server on a port via './gradlew startVaccinationsWireMockServer -PportNumber=8081'
2. Update the environment variables defined in 'cleanBuildTestAndRun.ps1' with your MongoDB URI (MONGO_DB_URI) and stub
   server URL (VACCINATIONS_URL).
3. Start the microservice via '.\cleanBuildTestAndRun.ps1  '

The API is defined by the OpenAPI specification `pet-store-api.yaml` and can be viewed in the Swagger
Editor https://editor.swagger.io/

The microservice is structured with Controller and Service layers.
Depending on the use case it may be desirable to also include a mapping layer to translate between one or more of the
following:

- API model types (pet store)
- External API call - API model types (vaccinations)
- Datastore/Repository entity types (MongoDB)

The API provides the following functionality:

- New pets can be added to the pet store.
- Customers can search for a pet.
- Pets can be updated when details change.
- Customers can purchase a pet.
- Pet vaccination details are provided via a 3rd party API call.

---

#### Alex TODO

Add unit tests
description of the purpose and role of this project
Alternative frameworks and options
Postman Collection integrated via a CI tool
Reduce duplication in var/path names e.g. 'external'
Add WebSecurity OAuth2?
Add tracing in logging

---

#### OpenAPI contract driven development

See build.gradle for an example of using OpenAPI schemas (pet-store-api.yaml and animal-vaccination-api.yaml)
to generate model classes and the Java interfaces for the APIs.  
By having a controller implement the Java interface for the API, when the OpenAPI schema is updated some
breaking changes will force a compile time error.

---

#### Data persistence via MongoDB

This service is integrated with a MongoDB NoSQL database using spring-boot-starter-data-mongodb
and the MongoRepository.java interface. Connection details are defined in the application.yaml under
'spring.data.mongodb'

For a more lightweight and simpler MongoDB integration consider using
the [https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-sync/4.11.1](mongodb-driver-sync)
client library. Although the out-of-the-box features of MongoRepository and @Document will not be available.


---

#### Exception handling

See the integration test `shouldReturnErrorWhenCallingGetPetEndpointWithInvalidIdFailingValidation`  
and the implementation `GlobalExceptionHandler`

---

#### Custom request validation

See `PetValidator` and the integration test
`shouldReturnErrorWhenCallingGetPetEndpointWithInvalidId`

---

#### Request validation via OpenAPI specification

See `/pets/{petId}` GET endpoint and the `petId schema` definition in `pet-store-api.yaml`
and the associated integration
test `shouldReturnErrorWhenCallingGetPetEndpointWithIdLargerThanPermitted`

---

#### Custom Enum serialisation/deserialisation logic

See PetMapper for an example of how to define Enum/String serialisation/deserialisation using MapStruct.

---

#### Unit tests
See JUnit tests in spring-boot-template-service\src\test\java which do not have the annotation @Tag("integration") or extend BaseIntegrationTest.java

---

#### Integration tests

See the test package integration for examples of integration tests utilising WireMock and de.flapdoodle embedded
MongoDB.

---

#### Metrics endpoint
See the metrics endpoint provided by Spring Actuator https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints
- GET http://localhost:8099/actuator/metrics

---

#### External REST API calls via Spring WebFlux

See VaccinationsApiClient for an example of calling an external REST API using Spring Flux.

---

#### Stubbing of external API calls via WireMock

See https://wiremock.org/docs/stubbing/ for additional guidance with WireMock.

Also see 'VaccinationsWireMockServer' for how to define a stub server for running a microservice locally with external
API dependencies.

In addition, 'WiremockServerForIntegrationTests' for how to utilise WireMock for automated integration tests.

---

#### Logging of requests and responses

See RequestLoggingFilterConfig for the required config to log requests.

Also consider the use of LoggingAspects and the LogMethodArguments annotation to log method arguments,
and the LogMethodResponse to log method responses.

---

#### Failure recovery via Spring Retry

See VaccinationsApiClient for an example of how Spring WebClient can be used to retry failed external
API calls.

---

#### Actuator endpoints

Determined by dependencies ("OpenAPI/Swagger docs" in build.gradle),
application config (see application.yaml) and security config (see SecurityConfig)

https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints

- GET http://localhost:8099/actuator
- GET http://localhost:8099/actuator/prometheus
- GET http://localhost:8099/actuator/health
- GET http://localhost:8099/actuator/info
- GET http://localhost:8099/actuator/metrics
- GET http://localhost:8099/actuator/mappings

Where 8099 is the Management Server Port 'management.server.port'

#### Swagger documentation endpoints

OpenAPI endpoints to provide live up-to-date API documentation.

- http://localhost:8080/v3/api-docs
- http://localhost:8080/v3/api-docs.yaml
- http://localhost:8080/v3/api-docs/springdoc
- http://localhost:8080/v3/api-docs/swagger-config

Where 8080 is the Application Server Port 'server.port' 