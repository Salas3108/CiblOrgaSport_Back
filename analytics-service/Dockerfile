# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests package

# Runtime stage — Java 21 pour être cohérent avec le build
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8092

ENV DB_HOST=postgres \
    DB_PORT=5432 \
    DB_NAME=glop \
    DB_USER=admin \
    DB_PASSWORD=password

ENTRYPOINT ["java", "-jar", "/app/app.jar"]