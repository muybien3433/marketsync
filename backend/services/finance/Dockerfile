FROM openjdk:23-jdk AS build
WORKDIR /app
COPY target/finance-0.0.1.jar /app/finance-0.0.1.jar
EXPOSE 8800
ENTRYPOINT ["java", "-jar", "/app/finance-0.0.1.jar"]
