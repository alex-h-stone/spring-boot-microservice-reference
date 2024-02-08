# Spring Boot Microservice Template

Requirements:

* Java 21 e.g. https://jdk.java.net/21/
* Access to a running MongoDB instance or equivalent (e.g. Microsoft Azure CosmosDB
  via https://ensemble.ent.cgi.com/business/305832/serviceexcellence/Service%20Excellence%20Wiki%20Library/Sandbox.aspx)
* An IDE, Eclipse or IntelliJ (recommended)

This is a bare-bones Spring Boot 3 based microservice with working examples of many common business requirements.

The API of the microservice is defined by the API Specification `pet-store-api.yaml` and can be viewed by the online
swagger editor https://editor.swagger.io/

The microservice is structured with Controller and Service layers.
Depending on the use case it may be desirable to also include a mapping layer
to translate between one or more of the following:

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

#### Database integration with MongoDB

This service is integrated with a MongoDB NoSQL datastore/database using spring-boot-starter-data-mongodb
and the MongoRepository.java interface. Connection details are defined in the application.yaml under
'spring.data.mongodb'

---
#### Exception handler
See
`integration.cgi.example.petstore.ApplicationIntegrationTests.shouldReturnErrorWhenCallingGetPetEndpointWithInvalidId`  
and  
`src/main/java/com/example/springboottemplateservice/handler/GlobalExceptionHandler.java`
---

#### Request validation by a validator class

See `PetValidator.java`
and `ApplicationIntegrationTests.shouldReturnErrorWhenCallingGetPetEndpointWithInvalidId`

---

#### Request validation defined by yaml

See `/pets/{petId}` GET endpoint and the `petId schema` definition in `src/main/resources/openapi/pet-store-api.yaml`
and the associated integration
test `ApplicationIntegrationTests.shouldReturnErrorWhenCallingGetPetEndpointWithIdLargerThanPermitted`

---

#### Custom Enum serialisation/deserialisation logic
See PetAndPetDocumentMapper.java for an example of how to handle custom Enum serialisation logic using MapStruct.

---

#### Unit tests
See JUnit tests in spring-boot-template-service\src\test\java which do not have the annotation @Tag("integration") or extend BaseIntegrationTest.java

---

#### Integration tests

See the package src\test\java\com\cgi\example\petstore\integration for examples of integration tests utilising WireMock
and Embedded MongoDB.

---

#### Metrics endpoint
See the metrics endpoint provided by Spring Actuator https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints
- GET http://localhost:8099/actuator/metrics

---

#### OpenAPI/Interface driven API (including support for multiple OpenAPI schemas)
See build.gradle for an example of using OpenAPI schemas (pet-store-api.yaml and animal-vaccination-api.yaml)
to generate model classes and the Java interface for the API.  
By having a controller implement the Java interface for the API, when the OpenAPI schema is updated some
breaking changes will force a compile time error.

---

#### Calling another REST API using flux
See VaccinationsApiClient for an example of calling another REST API using Spring Flux.

---

#### Logging of requests and responses with configurable filter to avoid logging of 'typical' sensitive field names
TODO maybe use aspectJ and PII/truffle hog style filter?
Request logging via AOP

---

#### Spring Retry
TODO demonstrate with 3rd party API call

---

#### Actuator endpoints
Determined by dependencies (see "OpenAPI/Swagger docs" section in build.gradle),
application config (see application.yaml) and security config (see SecurityConfig.java)

https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints

- GET http://localhost:8099/actuator
- GET http://localhost:8099/actuator/prometheus
- GET http://localhost:8099/actuator/health
- GET http://localhost:8099/actuator/info
- GET http://localhost:8099/actuator/metrics
- GET http://localhost:8099/actuator/mappings

Where 8099 is the Management Server Port 'management.server.port'

#### OpenAPI/Swagger documentation endpoint
OpenAPI endpoints

- http://localhost:8080/v3/api-docs
- http://localhost:8080/v3/api-docs.yaml
- http://localhost:8080/v3/api-docs/springdoc
- http://localhost:8080/v3/api-docs/swagger-config

Where 8080 is the Application Server Port 'server.port' 