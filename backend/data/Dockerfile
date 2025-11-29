FROM amazoncorretto:25.0.1-alpine AS build
WORKDIR /app
COPY target/data-0.0.1.jar /app/data-0.0.1.jar
EXPOSE 8100
ENTRYPOINT ["java", "-jar", "/app/data-0.0.1.jar"]
