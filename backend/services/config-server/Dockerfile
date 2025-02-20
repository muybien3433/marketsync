FROM openjdk:23-jdk-slim
RUN apt-get update && apt-get install -y curl
WORKDIR /app
COPY target/config-server-1.0.jar /app/config-server-1.0.jar
EXPOSE 7777
ENTRYPOINT ["java", "-jar", "/app/config-server-1.0.jar"]
