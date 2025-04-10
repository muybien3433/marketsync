FROM openjdk:23-jdk AS build
WORKDIR /app
COPY target/wallet-0.0.1.jar /app/wallet-0.0.1.jar
EXPOSE 8200
ENTRYPOINT ["java", "-jar", "/app/wallet-0.0.1.jar"]
