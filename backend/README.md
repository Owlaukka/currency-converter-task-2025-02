# Currency Converter Backend

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## TODOs

- Figure out better way to mock GQL requests
- Add integration tests
- Docker-compose setup to mock Swop
- Caching setup for non-local

## Development (running the app etc.)

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_** Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

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

## Architecture and design

### Caching

Using Redis for caching because it is a separate cache which scales well and keeps the cached data separate from
the application itself leading to better UX and better cache hit-rate even through restarts. Due to this,
the application requires Docker to be running in dev because Quarkus starts a redis container using testcontainers.

Caching is done only on basic exchange-rate fetching level on the service level. This is pretty basic and could be
improved by adding custom caching logic to getEuroRatesForSourceAndTargetCurrency that would combine all rates fetched
together and access them from list instead of fetching them one by one. So basically you could then have two overlapping
fetches that populate the cache and a third request that uses the combined data from the other two cached requests.
That may or may not be beneficial but it adds complexity and felt it was not needed at this point without knowing the
actual user access patterns.