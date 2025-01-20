FROM openjdk:17-jdk-slim

WORKDIR /app

ARG FILE_DIRECTORY
ARG JWT_SECRET_KEY
ARG EUREKA_PROD_URL

ENV JWT_SECRET_KEY=$JWT_SECRET_KEY
ENV EUREKA_PROD_URL=$EUREKA_PROD_URL

COPY $FILE_DIRECTORY /app

RUN ls -al /app  # 빌드 중에 /app 디렉토리의 파일 나열

COPY /app/build/libs/*.jar /app.jar

ENTRYPOINT ["java","-jar","app.jar"]
