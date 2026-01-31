FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR from build stage
COPY target/resultat-service-*.jar app.jar

EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
