FROM amazoncorretto:25.0.1-alpine
RUN apk add --no-cache libgcc libstdc++
WORKDIR /app
COPY target/user-1.0.jar /app/user-1.0.jar
EXPOSE 8000
ENV JAVA_TOOL_OPTIONS="--enable-native-access=ALL-UNNAMED"
ENTRYPOINT ["java", "-jar", "/app/user-1.0.jar"]
