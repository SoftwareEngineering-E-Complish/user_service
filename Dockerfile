# Start with a base image containing Java runtime and Gradle
FROM gradle:7.2.0-jdk11 as build

# Set the working directory in the container
WORKDIR /app

# Copy the gradle configuration files
COPY build.gradle.kts settings.gradle.kts ./

# Copy the source code
COPY src src

# Build the application
RUN gradle build --no-daemon

# Start with a base image containing Java runtime
FROM openjdk:11-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file 
ENTRYPOINT ["java","-jar","/app.jar"]