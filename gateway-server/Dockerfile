FROM openjdk:17-jdk-slim

WORKDIR /app

ARG FILE_DIRECTORY
ARG JWT_SECRET_KEY
ARG EUREKA_PROD_URL

ENV JWT_SECRET_KEY=$JWT_SECRET_KEY
ENV EUREKA_PROD_URL=$EUREKA_PROD_URL

COPY $FILE_DIRECTORY /app

# gradlew 파일에 실행 권한 추가
RUN chmod +x gradlew

# gradle 빌드 실행 (build 디렉토리 생성)
RUN ./gradlew clean build -x test --no-daemon

# build 디렉토리 내에 있는 파일을 확인
RUN ls -al ./build/libs

COPY $FILE_DIRECTORY/build/libs/gateway-server-0.0.1-SNAPSHOT.jar ./app.jar

ENTRYPOINT ["java","-jar","app.jar"]

