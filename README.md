# Spring Boot Microservice Template

Spring Boot 3 based microservice template with a number of integrated features to address a majority of common
requirements.

Requirements to run locally:

* Java 21 e.g. https://jdk.java.net/21/
* MongoDB or equivalent (e.g. Microsoft Azure CosmosDB
  via CGI
  Sandbox https://ensemble.ent.cgi.com/business/305832/serviceexcellence/Service%20Excellence%20Wiki%20Library/Sandbox.aspx)
* An IDE, IntelliJ (recommended), or Eclipse

The API is defined by the OpenAPI specification `pet-store-api.yaml` and can be viewed in the Swagger
Editor https://editor.swagger.io/

The microservice is structured with Controller and Service layers.
Depending on the use case it may be desirable to also include a mapping layer to translate between one or more of the
following:

- API model types (pet store)
- 3rd Party - API model types (vaccinations)
- Datastore/Repository entity types (MongoDB)

The API provides the following functionality:

- New pets can be added to the pet store.
- Customers can search for a pet.
- Pets can be updated when details change.
- Customers can purchase a pet.
- Pet vaccination details are provided via a 3rd party API call.

---

#### OpenAPI/Interface driven API (including support for multiple OpenAPI schemas)

See build.gradle for an example of using OpenAPI schemas (pet-store-api.yaml and animal-vaccination-api.yaml)
to generate model classes and the Java interfaces for the APIs.  
By having a controller implement the Java interface for the API, when the OpenAPI schema is updated some
breaking changes will force a compile time error.

---

#### Data persistence using MongoDB

This service is integrated with a MongoDB NoSQL database using spring-boot-starter-data-mongodb
and the MongoRepository.java interface. Connection details are defined in the application.yaml under
'spring.data.mongodb'

---

#### Exception handling

See the integration test `shouldReturnErrorWhenCallingGetPetEndpointWithInvalidIdFailingValidation`  
and the implementation `GlobalExceptionHandler`

---

#### Custom request validation by a validator class

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

#### Making 3rd party REST API calls using Spring WebFlux
See VaccinationsApiClient for an example of calling another REST API using Spring Flux.

---

#### Logging of requests and responses with configurable filter to avoid logging of 'typical' sensitive field names

See RequestLoggingFilterConfig for the required config to log requests

TODO stonal use aspectJ and PII/truffle hog style filter? Or request logging via AOP?

---

#### Spring Retry

See VaccinationsApiClient and SpringRetryConfig for an example of how Spring Retry can be used to retry failed external
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

#### OpenAPI/Swagger documentation endpoint

OpenAPI endpoints to provide live up-to-date API documentation.

- http://localhost:8080/v3/api-docs
- http://localhost:8080/v3/api-docs.yaml
- http://localhost:8080/v3/api-docs/springdoc
- http://localhost:8080/v3/api-docs/swagger-config

Where 8080 is the Application Server Port 'server.port' 