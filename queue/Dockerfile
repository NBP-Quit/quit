FROM openjdk:17-jdk-slim

WORKDIR /app

ARG FILE_DIRECTORY

COPY $FILE_DIRECTORY /app

COPY /eureka-server/app/build/libs/*.jar /app.jar

ENTRYPOINT ["java","-jar","app.jar"]
