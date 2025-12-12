FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copie descripteur maven, récupération dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie code source, compilation
COPY src ./src
RUN mvn package -DskipTests

# image d'exécution
FROM openjdk:21-ea-jdk-slim
COPY --from=build /app/target/SyncStudy-1.0-SNAPSHOT-shaded.jar /app.jar

CMD ["java","-jar", "/app.jar"]