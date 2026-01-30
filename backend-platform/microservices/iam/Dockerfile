FROM amazoncorretto:25.0.1-alpine
RUN apk add --no-cache libgcc libstdc++
WORKDIR /app
COPY target/iam-1.0.jar /app/iam-1.0.jar
EXPOSE 8888
ENV JAVA_TOOL_OPTIONS="--enable-native-access=ALL-UNNAMED"
ENTRYPOINT ["java", "-jar", "/app/iam-1.0.jar"]
