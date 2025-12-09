FROM amazoncorretto:25.0.1-alpine
RUN apk add --no-cache libgcc libstdc++
WORKDIR /app
COPY target/finance-0.0.1.jar /app/finance-0.0.1.jar
EXPOSE 8800
ENV JAVA_TOOL_OPTIONS="--enable-native-access=ALL-UNNAMED"
ENTRYPOINT ["java", "-jar", "/app/finance-0.0.1.jar"]
