# 1단계: 빌드 스테이지
# Gradle 8.5와 JDK 17이 포함된 이미지를 사용하여 빌드 속도를 높이고 설치 오류를 방지합니다.
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# 빌드 캐시 효율을 위해 설정 파일 먼저 복사
COPY build.gradle settings.gradle /app/
COPY src /app/src

# Gradle 데몬을 끄고(-x test로 테스트 제외, 필요 시 삭제) 빌드 실행
# 이렇게 하면 직접 Gradle을 설치할 필요가 없습니다.
RUN gradle clean build -x test --no-daemon

# 2단계: 실행 스테이지
# 실행 시에는 무거운 Gradle 환경이 필요 없으므로 가벼운 JRE 이미지를 사용합니다.
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일만 추출하여 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 필요한 디렉토리 생성 (업로드 파일 보관용 등)
RUN mkdir -p /app/uploads

# 애플리케이션 포트 설정
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
