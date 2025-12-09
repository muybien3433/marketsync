FROM amazoncorretto:25.0.1-alpine
RUN apk add --no-cache libgcc libstdc++
WORKDIR /app
COPY target/subscription-0.0.1.jar /app/subscription-0.0.1.jar
EXPOSE 8900
ENV JAVA_TOOL_OPTIONS="--enable-native-access=ALL-UNNAMED"
ENTRYPOINT ["java", "-jar", "/app/subscription-0.0.1.jar"]
