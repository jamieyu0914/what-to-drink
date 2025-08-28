# 使用官方 OpenJDK 17 作為基底
FROM openjdk:17-jdk-slim

# 設定工作目錄
WORKDIR /app

# 複製 Maven Wrapper 和專案
COPY backend/pom.xml ./pom.xml
COPY backend/mvnw ./mvnw
COPY backend/.mvn ./.mvn
COPY backend/src ./src

# 確保 mvnw 可執行
RUN chmod +x mvnw

# Build Spring Boot jar
RUN ./mvnw clean package -DskipTests

# 啟動
ENTRYPOINT ["java","-jar","target/demo-0.0.1-SNAPSHOT.jar"]


