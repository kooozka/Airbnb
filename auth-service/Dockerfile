FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY . .
COPY libs/my-lib.jar /root/.m2/repository/com/airbnb/my-lib/1.0.0/my-lib-1.0.0.jar


RUN mvn clean package -DskipTests -pl notification-service -am

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/auth-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]