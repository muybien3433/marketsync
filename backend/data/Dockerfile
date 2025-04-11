FROM openjdk:23-jdk AS build
WORKDIR /app
COPY target/data-0.0.1.jar /app/data-0.0.1.jar
EXPOSE 8100
ENTRYPOINT ["java", "-jar", "/app/data-0.0.1.jar"]
