# syntax=docker/dockerfile:1

# --- Build stage ---
FROM maven:3-amazoncorretto-25 AS build
WORKDIR /build

# Cache dependencies first
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline

# Build application
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B clean package -DskipTests

# --- Runtime stage ---
FROM amazoncorretto:25-alpine AS runtime
WORKDIR /app

# Non-root user
RUN addgroup -S app && adduser -S app -G app

COPY --from=build /build/target/*.jar app.jar
RUN chown -R app:app /app
USER app

EXPOSE 8080

# spring-boot-docker-compose must be disabled inside the container
ENV SPRING_DOCKER_COMPOSE_ENABLED=false

ENTRYPOINT ["java", "-jar", "app.jar"]
