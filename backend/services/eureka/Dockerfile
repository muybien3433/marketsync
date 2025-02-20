FROM openjdk:23-jdk-slim
RUN apt-get update && apt-get install -y curl
WORKDIR /app
COPY target/eureka-1.0.jar /app/eureka-1.0.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app/eureka-1.0.jar"]
