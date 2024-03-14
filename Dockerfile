# Use a base image with JDK 11 and Maven pre-installed
FROM maven:3.6.3-openjdk-11 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project
COPY ./src/main ./src/main
COPY ./mvnw ./mvnw
COPY ./pom.xml ./pom.xml

# Package the application using parallel builds and go offline
#RUN mvn -T 1C clean package -DskipTests
RUN --mount=type=cache,target=/root/.m2,rw mvn clean install -DskipTests

# Create a new image with JRE only
FROM openjdk:11-jdk-slim

# Copy the packaged jar file from the build stage to the new image
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Define the command to run the application when the container starts
CMD ["java", "-jar", "app.jar"]
