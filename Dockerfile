FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY . .
ARG MAVEN_OPTS="-Xmx1024m -XX:MaxMetaspaceSize=384m"
RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:InitialRAMPercentage=25 -XX:MaxRAMPercentage=75 -XX:MaxMetaspaceSize=256m"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
