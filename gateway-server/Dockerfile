FROM openjdk:17-jdk-slim

WORKDIR /app

ARG FILE_DIRECTORY
ARG JWT_SECRET_KEY
ARG EUREKA_PROD_URL

ENV JWT_SECRET_KEY=$JWT_SECRET_KEY
ENV EUREKA_PROD_URL=$EUREKA_PROD_URL

COPY $FILE_DIRECTORY /app

RUN echo "file Directory $FILE_DIRECTORY"
RUN ls -al
RUN cd ./build && ls -al
RUN cd ./build/libs/ && ls -al
RUN cd ../../../

COPY $FILE_DIRECTORY/build/libs/gateway-server-0.0.1-SNAPSHOT.jar ./app.jar

ENTRYPOINT ["java","-jar","app.jar"]

