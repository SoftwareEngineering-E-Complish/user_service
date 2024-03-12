# Use a base image with JDK 11 and Maven pre-installed
FROM maven:3.6.3-openjdk-11 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project
COPY . .

# Package the application
RUN mvn clean package -DskipTests

# Create a new image with JRE only
FROM openjdk:11-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file from the build stage to the new image
COPY --from=build /app/target/user_service-1.0.0.jar .

# Expose the port the application runs on
EXPOSE 8080

# Define the command to run the application when the container starts
CMD ["java", "-jar", "user_service-1.0.0.jar"]
