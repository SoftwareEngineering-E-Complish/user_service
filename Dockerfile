# Use a base image with JDK 17 (or higher) and Maven pre-installed
FROM maven:3.6.3-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project
COPY ./src ./src
COPY ./mvnw ./mvnw
COPY ./pom.xml ./pom.xml

ARG USER_SERVICE_SONAR_PROJECT_KEY
ARG USER_SERVICE_SONAR_ORGANIZATION_KEY
ARG USER_SERVICE_SONAR_TOKEN
# Package the application using parallel builds and go offline
RUN --mount=type=cache,target=/root/.m2,rw mvn -T 8 clean verify sonar:sonar -Dsonar.login=$USER_SERVICE_SONAR_TOKEN -Dsonar.qualitygate.wait=true

# Create a new image with JRE only
FROM openjdk:17-jdk-slim

# Copy the packaged jar file from the build stage to the new image
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Define the command to run the application when the container starts
CMD ["java", "-jar", "app.jar"]
