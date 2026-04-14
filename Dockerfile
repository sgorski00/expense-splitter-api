FROM maven:3.9.12-eclipse-temurin-25-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY ./src ./src

RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:25-jdk-alpine AS dev
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]

FROM eclipse-temurin:25-jre-alpine AS runtime

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]