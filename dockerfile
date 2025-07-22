FROM openjdk:17-jdk-alpine
VOLUME /tmp

# 로그 디렉토리 생성
RUN mkdir -p /app/logs

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 로그 디렉토리 권한 설정
RUN chmod 755 /app/logs

ENTRYPOINT ["java","-jar","/app.jar"]