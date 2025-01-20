FROM openjdk:17-jdk-slim

WORKDIR /app

ARG FILE_DIRECTORY
ARG EUREKA_PROD_URL

ENV EUREKA_PROD_URL=$EUREKA_PROD_URL

COPY $FILE_DIRECTORY /app

RUN echo "file Directory $FILE_DIRECTORY"
RUN ls -al
RUN cd ./build/libs && ls -al

COPY ./build/libs/eureka-server-0.0.1-SNAPSHOT.jar ./app.jar

ENTRYPOINT ["java","-jar","app.jar"]
