FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/ms-cursos-consumer-1.0.0.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
