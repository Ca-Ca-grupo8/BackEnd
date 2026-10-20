FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY estoque/pom.xml estoque/pom.xml
COPY estoque/src estoque/src
RUN mvn --batch-mode -f estoque/pom.xml -DskipTests package

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build --chown=spring:spring /workspace/estoque/target/estoque-0.0.1-SNAPSHOT.jar /app/app.jar

USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
