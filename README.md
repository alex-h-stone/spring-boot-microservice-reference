# Spring Boot Microservice Template

Spring Boot 3 based microservice template integrating features which address a majority of common business requirements.

# Table of Contents

1. [Introduction and Purpose](#introduction-and-purpose)
2. [Run the Pet Store Microservice](#run-the-pet-store-microservice)
3. [Run Unit and Integration Tests](#run-unit-and-integration-tests)
4. [Dependency Version Management](#dependency-version-management)
5. [OpenAPI/Swagger Code Generation](#openapiswagger-code-generation)
6. [Data Persistence via MongoDB](#data-persistence-via-mongodb)
7. [Exception Handling](#exception-handling)
8. [Request Validation via OpenAPI Specification](#request-validation-via-openapi-specification)
9. [Java Request Validation](#java-request-validation)
10. [Mapping between Pet Store API model, external API model and MongoDB Documents](#mapping-between-pet-store-api-model-external-api-model-and-mongodb-documents)
11. [Unit Tests](#unit-tests)
12. [Integration Tests](#integration-tests)
13. [Dynamic port allocation and discovery for local development](#dynamic-port-allocation-and-discovery-for-local-development)
14. [Metrics Endpoint](#metrics-endpoint)
15. [External REST API Call with Retry via Spring WebFlux](#external-rest-api-call-with-retry-via-spring-webflux)
16. [Stubbing of External API Calls via WireMock](#stubbing-of-external-api-calls-via-wire-mock)
17. [Logging of Requests and Responses](#logging-of-requests-and-responses)
18. [Logging with a Mapped Diagnostic Context (MDC)](#logging-with-a-mapped-diagnostic-context-mdc)
19. [Structured JSON logging](#structured-json-logging)
20. [Execute Postman API test Collection as a Gradle task](#execute-postman-api-test-collection-as-a-gradle-task)
21. [Load Testing](#load-testing)
22. [Actuator Endpoints](#actuator-endpoints)
23. [Swagger Documentation Endpoints](#swagger-documentation-endpoints)
24. [Automated Code Style Formatting](#automated-code-style-formatting)

---

#### Introduction and Purpose

This template is designed to serve as a go-to reference for implementing many Spring Boot microservice features.
It is _representative_ of a real-life production ready microservice, although clearly depending on your specific 
requirements you will likely have to make some modifications to the approaches included in this project.

At a minimum, the template should provide a high quality default starting point for new features.

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
- Customers can search for a pet by either Id, or PetAvailabilityStatus.
- Pets can be updated when details change.
- Customers can purchase a pet.
- Pet vaccination details are provided via an external API call.
- Pets can be removed (archived) from the pet store.

---

#### Run the Pet Store microservice
Requirements to run locally:

* Java 21 e.g. https://jdk.java.net/21/
* An IDE, IntelliJ (recommended), or Eclipse
* (Nice to have) MongoDB or equivalent (e.g. Microsoft Azure CosmosDB via [CGI
  Sandbox](https://ensemble.ent.cgi.com/business/305832/serviceexcellence/Service%20Excellence%20Wiki%20Library/Sandbox.aspx))

When the above requirements have been satisfied, to start the Pet Store microservice:

1. Start the Wire Mock stub server:  
   `./gradlew startEmbeddedWireMock`
2. Start the in-memory MongoDB server:  
   `./gradlew startEmbeddedMongoDB`
3. Start the microservice:  
   `./gradlew bootRun --args='--spring.profiles.active=local'`
4. Execute the load tests:  
   `./gradlew :load-test:run`
5. Execute the API tests:  
   `./gradlew :api-test:run`

Alternatively, you can execute all of the above by opening a terminal (IntelliJ) or command prompt (cmd) and executing
the script:  
WIP `.\cleanBuildTestAndRunLocally.ps1` WIP

---

#### Run Unit and Integration tests

The integration tests are identified by the JUnit annotation `@Tag("integration")` which is also present in
[BaseIntegrationTest.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2FBaseIntegrationTest.java)
which is typically extended by integration tests.  
While unit tests are identified by the JUnit annotation `@Tag("unit")`.

By default, all tests (unit and integration) are run:  
`./gradlew test`

---

#### Dependency Version Management

To best manage a large number of Spring dependencies with independent version numbers this template uses the
following Spring Gradle plugins:

- `org.springframework.boot`
- `io.spring.dependency-management`

In addition to the dependency management BOM's:

- `org.springframework.boot:spring-boot-dependencies`
- `org.junit:junit-bom`

With all Gradle dependency and plugin versions defined in one place [gradle.properties](gradle.properties).

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
and the `MongoRepository` interface. Connection details are defined in the `application.yaml` as a connection string 
using the property `spring.data.mongodb.uri`.

For a more lightweight and simpler MongoDB integration consider using
the [mongodb-driver-sync](https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-sync/4.11.1)
client library. Although the out-of-the-box features of `MongoRepository` and `@Document` will not be available.

---

#### Exception Handling

Any exceptions which are thrown by the microservice will be caught and handled by the `GlobalExceptionHandler`.
All application exceptions extend `AbstractApplicationException` which allows you to specify both a message and 
the HTTP status code which should be used in the response.

---

#### Request validation via OpenAPI specification

See `/pets/{petId}` GET endpoint and the `petId schema` definition in `pet-store-api.yaml`
and the associated integration
test `shouldReturnErrorWhenCallingGetPetEndpointWithIdLargerThanPermitted` for details of how request validation can
be implemented via the API yaml.

---

#### Java Request Validation

See [PetIdValidator.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fcontroller%2Fvalidation%2FPetIdValidator.java) and the integration test
`should_ReturnError_When_CallingGetPetEndpointWithInvalidIdFailingValidation`  
for details of how to write custom request validation logic.

Wherever possible, request validation logic should be defined in the OpenAPI definition e.g. [pet-store-api.yaml](src%2Fmain%2Fresources%2Fopenapi%2Fpet-store-api.yaml)  
This has several benefits over custom Java validators, including:
- Reusable and language agnostic, such that you can use the OpenAPI yaml to rewrite the microservice using another (non-Java) implementation language.
- The validation is visible by all consumers of the microservice via the OpenAPI yaml.
- The API documentation will always be up-to-date with the validation implementation.

---

#### Mapping between Pet Store API model, external API model and MongoDB Documents

See the below classes for examples of different mappers:
- [PetMapper.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fservice%2Fpet%2FPetMapper.java)
- [ExternalVaccinationsMapper.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fexternal%2Fvaccinations%2FExternalVaccinationsMapper.java)
- [CustomerMapper.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fservice%2Fcustomer%2FCustomerMapper.java)

Depending on the use case [MapStruct](https://mapstruct.org/) is also an option for reducing boilerplate mapping code. 
This should to be weighed up against the increase in complexity of the mapping implementation.

---

#### Unit Tests

All JUnit based unit tests have the annotation `@Tag("unit")` so they can be easily identified and executed
independently of slower running integration tests.

To run only unit tests:  
`./gradlew -PincludeTag=unit test`  
or  
`./gradlew -PexcludeTag=integration test`

---

#### Integration Tests

All JUnit based integration tests have the annotation `@Tag("integration")` so they can be easily identified and
executed when required.

Typically, integration tests extend the base
class [BaseIntegrationTest.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2FBaseIntegrationTest.java)
and use Wire
Mock [WireMockForIntegrationTests.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2Futils%2FWireMockForIntegrationTests.java)
and `de.flapdoodle.embed.mongo` [MongoDbForIntegrationTests.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2Futils%2FMongoDbForIntegrationTests.java)
to stand-in for external dependencies.

To run only integration tests:  
`./gradlew -PincludeTag=integration test`  
or  
`./gradlew -PexcludeTag=unit test`

---

#### Dynamic port allocation and discovery for local development

To improve developer efficiency, when running the microservice and associated dependencies like Wire Mock and Embedded
MongoDB locally, all port numbers are assigned dynamically and subsequently discovered when needed via the
[DynamicApplicationPropertiesRepository.java](common%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fcommon%2Flocal%2FDynamicApplicationPropertiesRepository.java).

For example, when you start the [EmbeddedWireMock.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fembedded%2FEmbeddedWireMock.java) the port number which it is listening on persisted so that 
when you start the microservice (with the `local` profile) it will be automatically configured to use the correct 
Wire Mock port.

Port allocation and discovery includes:
- Embedded Wire Mock `./gradlew startEmbeddedWireMock`
- Embedded MongoDB `./gradlew startEmbeddedMongoDB`
- Pet Store microservice `./gradlew bootRun --args='--spring.profiles.active=local'`
- API Tests `./gradlew :api-test:run`
- Load Tests `./gradlew :load-test:run`

---

#### Metrics Endpoint

See the metrics endpoint provided by Spring Actuator https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints
- GET http://localhost:8099/actuator/metrics

---

#### External REST API call with retry via Spring WebFlux

See [VaccinationsApiClient.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fexternal%2Fvaccinations%2FVaccinationsApiClient.java) 
for an example of making an external REST API call with retry logic using the SpringFlux `WebClient`.

---

#### Stubbing of external API calls via Wire Mock

See https://wiremock.org/docs/stubbing/ for additional guidance with Wire Mock.

Also see [EmbeddedWireMock.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fembedded%2FEmbeddedWireMock.java) for how to run a stand-alone embedded stub server for running a microservice 
locally which has external API dependencies.

In
addition, [WireMockForIntegrationTests.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2Futils%2FWireMockForIntegrationTests.java)
for how to utilise Wire Mock for automated integration tests.

---

#### Logging of requests and responses

See [RequestLoggingFilterConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fconfig%2FRequestLoggingFilterConfiguration.java) 
for the required config to log requests using the `CommonsRequestLoggingFilter`.

Also consider the use of AOP [LoggingAspects.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Flogging%2FLoggingAspects.java) 
and the `@LogMethodArguments` annotations to log method arguments,
and the `@LogMethodResponse` to log the method return object.

---

#### Logging with a Mapped Diagnostic Context (MDC)

To improve the traceability of user actions when looking at the microservice logs there is a Mapped Diagnostic Context (MDC).
This class [MappedDiagnosticContextFilter.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fconfig%2FMappedDiagnosticContextFilter.java)
ensures that every log statement includes both the `userId` and `remoteAddress`.  
Making retrieving logs for a specific user or remote system trivial.

Consider also including a unique session Id in the MDC, provided by an API Gateway.

---

#### Structured JSON Logging

To improve the search-ability of application logs they are structured using JSON to provide a balance between both 
machine and developer readability.

This is implemented using the dependency `net.logstash.logback:logstash-logback-encoder` and the appropriate config in 
[logback.xml](src%2Fmain%2Fresources%2Flogback.xml).

---

#### Execute Postman API test Collection as a Gradle task

Install `Node.js` version greater than 16 as
per https://github.com/postmanlabs/newman?tab=readme-ov-file#getting-started.

On Windows this is best done via [nvm](https://github.com/coreybutler/nvm-windows/releases).  
After `nvm` has been installed, execute the command `nvm list available` to see which versions of `Node.js` are
available to install, now install a compatible version of `Node.js` e.g. `nvm install 21.7.2`.  
Now use `nvm` to select the updated `Node.js` version e.g. `nvm use 21.7.2`.

Now install the Postman Collections Runner `Newman` via: `npm install -g newman`.  
And the additional Newman HTML reporting feature: `npm install -g newman-reporter-htmlextra`

**Note**, you may require to temporarily disable npm ssl: `npm config set strict-ssl false`

To execute the Postman Collection execute the gradle task:
`./gradlew :api-test:run`

The API test is implemented via [ApiTestApplication.java](api-test%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fapitest%2FApiTestApplication.java)
which uses the required port numbers from [DynamicApplicationPropertiesRepository.java](common%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fcommon%2Flocal%2FDynamicApplicationPropertiesRepository.java) 
to execute Newman with the required command line arguments.

---

#### Load Testing

Load testing is implemented using `Gatling` for which the dependencies are defined in [load-test/build.gradle](load-test%2Fbuild.gradle).
It is executed via the [LoadTestApplication.java](load-test%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Floadtest%2FLoadTestApplication.java) class.  
The load test scenarios are defined in [LoadSimulationDefinition.java](load-test%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Floadtest%2FLoadSimulationDefinition.java).  

There is also a metrics collection feature, which polls the JVM memory usage using the actuator metrics endpoint. 
The metrics are recorded and reported on following the load test. See [MemoryUsageMetrics.java](load-test%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Floadtest%2Fmemory%2FMemoryUsageMetrics.java) 
for implementation details.

All required port numbers are configured dynamically in [HttpProtocolBuilders.java](load-test%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Floadtest%2FHttpProtocolBuilders.java) using the [DynamicApplicationPropertiesRepository.java](common%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fcommon%2Flocal%2FDynamicApplicationPropertiesRepository.java).   


---

#### Actuator Endpoints

Determined by the "OpenAPI/Swagger docs" dependencies in the [build.gradle](build.gradle),  
application config [application.yaml](src%2Fmain%2Fresources%2Fapplication.yaml)  
and security config [SecurityConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fconfig%2FSecurityConfiguration.java).

https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints

- GET http://localhost:8099/actuator
- GET http://localhost:8099/actuator/prometheus
- GET http://localhost:8099/actuator/health
- GET http://localhost:8099/actuator/info
- GET http://localhost:8099/actuator/metrics
- GET http://localhost:8099/actuator/mappings

Where `8099` is the Management Server Port `management.server.port`

---

#### Swagger Documentation Endpoints

With the [OpenApiConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fconfig%2FOpenApiConfiguration.java) 
endpoints are provided to provide live up-to-date API documentation.

- http://localhost:8080/swagger-ui.html
- http://localhost:8080/v3/api-docs/swagger-config
- http://localhost:8080/v3/api-docs
- http://localhost:8080/v3/api-docs.yaml
- http://localhost:8080/v3/api-docs/springdoc


Where `8080` is the Application Server Port `server.port`

---

#### Automated Code Style Formatting

Ensure all Java files are formatted consistently to the Google Java Coding Standards and apply the
formatting with every build via the [Diff Plug - Spotless](https://github.com/diffplug/spotless) Gradle plugin.

Code formatting can be applied to the project via:  
`./gradlew spotlessApply`

And verified via:  
`./gradlew spotlessCheck`

See `spotless` in [build.gradle](build.gradle) for details.