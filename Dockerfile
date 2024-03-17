FROM maven:3.6.3-openjdk-17 AS build

WORKDIR /app

COPY ./src ./src
COPY ./mvnw ./mvnw
COPY ./pom.xml ./pom.xml

RUN --mount=type=cache,target=/root/.m2,rw mvn -T 8 clean package

FROM openjdk:17-jdk-slim

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
