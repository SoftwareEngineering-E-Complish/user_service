# Use a base image with JDK 17 and Maven pre-installed
FROM maven:3.9.6-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project
COPY . .

# Package the application
RUN mvn clean package -DskipTests

# Create a new image with JRE only
FROM openjdk:17-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file from the build stage to the new image
COPY --from=build /app/target/your-application.jar .

# Expose the port the application runs on
EXPOSE 8080

# Define the command to run the application when the container starts
CMD ["java", "-jar", "your-application.jar"]
