FROM amazoncorretto:25.0.1-alpine AS build
WORKDIR /app
COPY target/subscription-0.0.1.jar /app/subscription-0.0.1.jar
EXPOSE 8900
ENTRYPOINT ["java", "-jar", "/app/subscription-0.0.1.jar"]
