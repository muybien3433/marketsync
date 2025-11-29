FROM amazoncorretto:25.0.1-alpine AS build
WORKDIR /app
COPY target/notification-0.0.1.jar /app/notification-0.0.1.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "/app/notification-0.0.1.jar"]
