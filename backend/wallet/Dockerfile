FROM amazoncorretto:25.0.1-alpine
RUN apk add --no-cache libgcc libstdc++
WORKDIR /app
COPY target/wallet-0.0.1.jar /app/wallet-0.0.1.jar
EXPOSE 8200
ENV JAVA_TOOL_OPTIONS="--enable-native-access=ALL-UNNAMED"
ENTRYPOINT ["java", "-jar", "/app/wallet-0.0.1.jar"]
