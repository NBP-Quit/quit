FROM openjdk:17-jdk-slim

VOLUME /tmp
# 현재 작업 디렉토리 출력
RUN echo "Current directory: $(pwd)"

# 빌드 경로 출력 (예: /eureka-server/build/libs)
RUN ls -l

COPY ./tmp/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]