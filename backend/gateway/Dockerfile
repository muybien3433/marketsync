FROM amazoncorretto:25.0.1-alpine
WORKDIR /app
COPY target/gateway-1.0.jar /app/gateway-1.0.jar
EXPOSE 9999
ENTRYPOINT ["java", "-jar", "/app/gateway-1.0.jar"]
