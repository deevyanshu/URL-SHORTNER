From maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

From openjdk:17.0.1-jdk-slim
COPY --from=build /target/urlshortner-0.0.1-SNAPSHOT.jar urlshortner.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","urlshortner.jar"]
