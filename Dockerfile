# 1. JDK 이미지 기반
FROM openjdk:21-jdk

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 로컬에서 빌드한 jar 파일을 컨테이너에 복사
COPY build/libs/*SNAPSHOT.jar just_one_bite.jar

# 4. 포트 설정 (Spring Boot 기본 8080)
EXPOSE 8080

# 5. 컨테이너 시작 시 실행할 명령
ENTRYPOINT ["java", "-jar", "just_one_bite.jar"]