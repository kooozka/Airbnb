FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests -pl rating-service -am

FROM openjdk:17-jdk-slim

WORKDIR /app

ENV JWT_SECRET = ""

COPY --from=build /app/rating-service/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
