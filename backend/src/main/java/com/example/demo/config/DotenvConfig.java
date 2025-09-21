package com.example.demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadEnvironmentVariables() {
        try {
            // 載入 .env 文件
            Dotenv dotenv = Dotenv.configure()
                    .directory("./") // 從項目根目錄載入
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            // 將 .env 中的變數設置為系統屬性
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // 只設置尚未存在的系統屬性
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
                
                System.out.println("已載入環境變數: " + key + " = " + 
                    (key.toLowerCase().contains("secret") || key.toLowerCase().contains("password") ? 
                     value.substring(0, Math.min(4, value.length())) + "..." : value));
            });
            
            System.out.println("✅ .env 文件載入完成");
            
        } catch (Exception e) {
            System.out.println("⚠️  載入 .env 文件時發生錯誤: " + e.getMessage());
            System.out.println("將使用預設配置或環境變數");
        }
    }
}
