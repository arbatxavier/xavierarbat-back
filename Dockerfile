# Stage 1: Build
FROM gradle:8.14-jdk17 AS build
WORKDIR /app

# Copy configuration files first to cache dependencies
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# Copy source code and build the JAR
# -x test: skips tests to save memory and time during deployment
# -Dorg.gradle.jvmargs="-Xmx2g": limits memory usage to prevent OOM
COPY src ./src
RUN gradle bootJar -x test --no-daemon -Dorg.gradle.jvmargs="-Xmx2g"

# Stage 2: Run
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
