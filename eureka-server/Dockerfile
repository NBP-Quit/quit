FROM openjdk:17-jdk-slim

WORKDIR /app

ARG FILE_DIRECTORY
ARG EUREKA_PROD_URL

ENV EUREKA_PROD_URL=$EUREKA_PROD_URL

COPY ../$FILE_DIRECTORY /app

COPY /app/build/libs/*.jar /app.jar

ENTRYPOINT ["java","-jar","app.jar"]
