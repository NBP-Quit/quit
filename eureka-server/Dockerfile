FROM openjdk:17-jdk-slim

WORKDIR /app

ARG FILE_DIRECTORY
ARG SERVICE
RUN echo " 현재 작업 디렉토리 : ${FILE_DIRECTORY}"
RUN echo " 현재 작업 서비스 : ${SERVICE}"
RUN ls -al

ARG EUREKA_PROD_URL

ENV EUREKA_PROD_URL=$EUREKA_PROD_URL

COPY $FILE_DIRECTORY /app

COPY $FILE_DIRECTORY/build/libs/eureka-server-0.0.1-SNAPSHOT.jar ./app.jar

ENTRYPOINT ["java","-jar","app.jar"]
