FROM amazoncorretto:25.0.1-alpine
RUN apk add --no-cache libgcc libstdc++
WORKDIR /app
COPY target/gateway-1.0.jar /app/gateway-1.0.jar
EXPOSE 9999
ENV JAVA_TOOL_OPTIONS="--enable-native-access=ALL-UNNAMED"
ENTRYPOINT ["java", "-jar", "/app/gateway-1.0.jar"]
