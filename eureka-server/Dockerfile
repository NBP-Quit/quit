FROM openjdk:17-jdk-slim

VOLUME /tmp

RUN ls build/libs

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
