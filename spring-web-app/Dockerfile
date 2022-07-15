# syntax=docker/dockerfile:1

FROM openjdk:17-alpine3.13 as base

WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src

FROM base as test
RUN ["./mvnw", "test"]

FROM base as development
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments="'agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"]

FROM base as build
RUN ./mvnw package

FROM eclipse-temurin:17-jre-alpine as production
EXPOSE 8080
COPY --from=build /app/target/demo-*.jar /demo.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/demo.jar"]



