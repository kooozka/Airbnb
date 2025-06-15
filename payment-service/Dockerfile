FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY . .

RUN mvn clean package -DskipTests -pl payment-service -am

FROM openjdk:17-jdk-slim

WORKDIR /app

ENV JWT_SECRET = ""

COPY --from=build /app/payment-service/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
