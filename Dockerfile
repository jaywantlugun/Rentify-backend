FROM gradle:7.2.0-jdk17 AS build
WORKDIR /app
COPY . /app

RUN chmod +x gradlew

RUN ./gradlew build -x test

FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/rentify-0.0.1-SNAPSHOT.jar rentify.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","demo.jar"]