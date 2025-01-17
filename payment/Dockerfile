FROM openjdk:17-jdk-slim

VOLUME /tmp

WORKDIR /app

ARG FILE_DIRECTORY

COPY $FILE_DIRECTORY /app

COPY /app/build/libs/*.jar /app.jar

ENTRYPOINT ["java","-jar","app.jar"]
