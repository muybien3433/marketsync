FROM eclipse-temurin:23-jdk-noble

RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY target/config-1.0.jar /app/config-1.0.jar
EXPOSE 7777
HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:7777/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/config-1.0.jar"]
