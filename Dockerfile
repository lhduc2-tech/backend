# Stage 1: Build the Spring Boot application
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Gradle wrapper and build configuration first for dependency caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# Copy source code and build the executable JAR
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run as non-root user
RUN groupadd --system app && useradd --system --gid app app
USER app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -XX:MaxRAMPercentage=95.0 -jar app.jar"]
