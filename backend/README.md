# Currency Converter Backend

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Architecture and Design

### Overview

The application is a REST API service that provides currency conversion functionality with the following key features:

- RESTful endpoints for currency conversion and currency listing
- Integration with Swop's GraphQL API for exchange rates
  - Reason mainly being that it provides the ability to generate code from the schema. Although I didn't have time to implement that code-generation.
- Redis-based caching with Quarkus cache extension
- Comprehensive fault tolerance patterns
- OpenAPI-driven development with automatic validation

### Key Components

1. **Currency Conversion Service (`CurrencyConversionService`)**

   - Handles currency amount conversion logic
   - Validates currency codes against Swop API
   - Provides 2-decimal precision with proper rounding
   - Caches conversion results

2. **Exchange Rate Integration (`ExchangeRateService` with `SwopExchangeRateIntegrationServiceImpl` implementation)**

   - GraphQL-based integration using SmallRye GraphQL client
   - Implements circuit breaker pattern
   - Retries failed requests
   - Bulkhead pattern for concurrent request limiting
   - Timeouts after 5 seconds

3. **API Layer**
   - OpenAPI 3.0 specification with automatic validation
     - Full specification available at [src/main/resources/openapi/api.yaml](src/main/resources/openapi/api.yaml)
   - Rate limiting on endpoints
   - Standardized error responses
   - Input validation for currency codes (basic pattern and length) and amounts

### REST API and OpenAPI Specification

The API provides two main endpoints:

- `/conversion` - Convert amounts between currencies
- `/currencies` - List supported currencies
  - Idea was to use this on the UI to already get all available currencies as quickly as possible, but ran out of time to implement this. It would require some mechanism to load them asynchronously without blocking inputs from the user in case the request fails.

Input validation includes:

- Currency codes must be 3 uppercase letters
- Amounts must be positive with max 2 decimal places
- All parameters are required

The REST API is designed following OpenAPI 3.0 specification standards. The API specification is maintained in:
[src/main/resources/openapi/api.yaml](src/main/resources/openapi/api.yaml)

Key features:

- API-first design approach using OpenAPI 3.0
- Automatic code generation for models and interfaces
- Type-safe client generation for frontend consumption

#### API Documentation

The OpenAPI specification is automatically exposed at runtime:

- Swagger UI: http://localhost:8080/q/swagger-ui/
- Raw OpenAPI spec: http://localhost:8080/q/openapi

These are currently very lacking because they don't seem to read the auto-generated code, so I ended up just manually copying the OpenAPI spec to the UI for code-generation there. This should be improved to be more robust.

#### Code Generation

The project uses OpenAPI Generator Maven plugin to generate:

- API interfaces
- Request/response models
- Type-safe validation annotations

To regenerate the API code:

```shell script
./gradlew openApiGenerate
```

Generated code is located in `build/generated/`.

### Caching Strategy

The application uses Redis as a distributed cache for several reasons:

- Scalability across multiple instances
- Persistence through application restarts
- High performance and low latency
- Support for complex data structures

Current caching implementation:

- Exchange rates and available currencies are cached at the service level
- Cache keys are based on currency pairs for exchange rates
- TTL is configured to balance freshness and performance
  - Can be configured with ENV-vars too

Potential improvements:

- Implement batch caching for multiple currency pairs
- Add predictive prefetching for common conversions
- Invalidate exchange-rates caching at midnight UTC (?)
- Serve data from cache when Swop integration is down
  - This is mainaly why I added date of rates in the responses so UI could know if the converted currency is possibly stale while still getting something

## Development

### Prerequisites

- JDK 21 or later
- Docker

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_** Quarkus Dev UI is available in dev mode at <http://localhost:8080/q/dev/>.

You'll need to add a valid Swop API-key as an ENV-variable `SWOP_API_KEY` in your preferred way in order for the application to work.

### Packaging and running the application

Docker is required to be running to run the app because the application uses Redis which is run using testcontainers
in dev.

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

### Building a Docker image

To build a Docker image using the JVM mode Dockerfile, run:

```shell script
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/currencyconverter-jvm .
```

You can then run the container using:

```shell script
docker run -i --rm -p 8080:8080 quarkus/currencyconverter-jvm
```

Note that a separate Redis instance needs to be running and connection to it configured (Not yet implemented).

### Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/currencyconverter-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

### Running the tests

You can run the tests using:

```shell script
./gradlew test
```

```

```
