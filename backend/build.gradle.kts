plugins {
    java
    id("io.quarkus")
    id("org.openapi.generator") version "7.11.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-jackson")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("io.quarkus:quarkus-smallrye-graphql-client")
    implementation("io.quarkus:quarkus-smallrye-fault-tolerance")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:2.35.1")
}

group = "me.owlaukka"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

openApiGenerate {
    generatorName.set("jaxrs-spec")
    inputSpec.set("$projectDir/src/main/resources/openapi/api.yaml")
    outputDir.set("${layout.buildDirectory.get()}/generated")
    apiPackage.set("me.owlaukka.api")
    modelPackage.set("me.owlaukka.model")
    typeMappings.set(mapOf(
        "decimal" to "java.math.BigDecimal"
    ))
    configOptions.set(mapOf(
        "interfaceOnly" to "true",
        "returnResponse" to "true",
        "useJakartaEe" to "true",
        "useSwaggerAnnotations" to "false",
        "useTags" to "true",
        "sourceFolder" to "src/gen/java",
        "serializationLibrary" to "jackson",
        "dateLibrary" to "java8",
        "skipDefaultInterface" to "true",
        "generateApiTests" to "false",
        "generateApiDocumentation" to "false",
        "generateSupportingFiles" to "false",
        "legacyTypeResolving" to "true"
    ))
}

sourceSets {
    main {
        java {
            srcDir("${layout.buildDirectory.get()}/generated/src/gen/java")
        }
    }
}

tasks.named("compileJava").configure {
    dependsOn("openApiGenerate")
}
