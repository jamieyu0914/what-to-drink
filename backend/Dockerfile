# 使用官方 OpenJDK 17 作為基底
FROM openjdk:17-jdk-slim

# 設定工作目錄
WORKDIR /app

# 注意
# 請手動在 /backend 目錄下先執行過 ./mvnw clean package -DskipTests 產生 jar 檔案

# 先檢查是否已經有編譯好的 jar 檔案
COPY target/demo-0.0.1-SNAPSHOT.jar* ./target/

# 如果沒有現成的 jar 檔案，才進行編譯
# 複製 Maven Wrapper 和 pom.xml
COPY pom.xml ./pom.xml
COPY mvnw ./mvnw
COPY .mvn ./.mvn

# 確保 mvnw 可執行
RUN chmod +x mvnw

# 複製原始碼
COPY src ./src

# 條件式建置：只有當 jar 檔案不存在時才編譯
RUN if [ ! -f "target/demo-0.0.1-SNAPSHOT.jar" ]; then \
    echo "JAR file not found, building from source..." && \
    ./mvnw clean package -DskipTests; \
    else \
    echo "JAR file found, skipping build..."; \
    fi

# 啟動
ENTRYPOINT ["java","-jar","target/demo-0.0.1-SNAPSHOT.jar"]


