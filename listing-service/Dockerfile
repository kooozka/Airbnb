FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests -pl listing-service -am

FROM openjdk:17-jdk-slim

WORKDIR /app

ENV JWT_SECRET = ""

COPY --from=build /app/listing-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
