FROM openjdk:23-jdk-slim
WORKDIR /app
COPY target/customer-0.0.1.jar /app/customer-0.0.1.jar
EXPOSE 8100
ENTRYPOINT ["java", "-jar", "/app/customer-0.0.1.jar"]
