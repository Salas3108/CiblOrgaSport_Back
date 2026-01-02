# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
# Use environment variables for DB config (matches application.properties)
ENV DB_HOST=postgres \
    DB_PORT=5432 \
    DB_NAME=glop \
    DB_USER=admin \
    DB_PASSWORD=password

# Ensure curl is available for healthcheck and wait-for script (Debian/Alpine differ; adapt if needed)
RUN apt-get update && apt-get install -y curl netcat && rm -rf /var/lib/apt/lists/*

# Copy wait-for script
COPY wait-for.sh /usr/local/bin/wait-for.sh
RUN chmod +x /usr/local/bin/wait-for.sh

# Use wait-for to block until Postgres is ready, then start the app
ENTRYPOINT ["java","-jar","/app/app.jar"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
