FROM eclipse-temurin:17-jdk-focal
COPY target/identity-reconciliation-0.0.1-SNAPSHOT.jar  identity-reconciliation-api.jar
ENTRYPOINT ["java" , "-jar", "/identity-reconciliation-api.jar"]