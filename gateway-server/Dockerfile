FROM openjdk:17-jdk-slim

WORKDIR /app

ARG FILE_DIRECTORY
ARG SERVICE
RUN echo " 현재 작업 디렉토리 : ${FILE_DIRECTORY}"
RUN echo " 현재 작업 서비스 : ${SERVICE}"

ARG JWT_SECRET_KEY
ARG EUREKA_PROD_URL

ENV JWT_SECRET_KEY=$JWT_SECRET_KEY
ENV EUREKA_PROD_URL=$EUREKA_PROD_URL

COPY $FILE_DIRECTORY /app

RUN ls -al
# build 디렉토리 내에 있는 파일을 확인
RUN pwd

COPY build/libs/gateway-0.0.1-SNAPSHOT.jar ./app.jar

ENTRYPOINT ["java","-jar","app.jar"]

