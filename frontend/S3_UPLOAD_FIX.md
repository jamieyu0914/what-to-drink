# S3 上傳 403 錯誤修復指南

## 問題描述

之前的實作嘗試直接使用 FormData 上傳到 S3，但沒有提供適當的身份驗證，導致 403 Forbidden 錯誤。

## 解決方案

現在的實作使用 AWS SDK 和適當的身份驗證方式來上傳檔案到 S3。

## 設定要求

### 方法 1：使用 Cognito Identity Pool（推薦）

這是最安全和推薦的方法，因為它使用臨時憑證並且不會在前端暴露永久的 AWS 憑證。

1. **創建 Cognito Identity Pool**：
   - 在 AWS 控制台中建立 Cognito Identity Pool
   - 設定經過身份驗證的角色，給予 S3 上傳權限
   - 將 User Pool 與 Identity Pool 連結

2. **設定環境變數**：

   ```bash
   VITE_COGNITO_IDENTITY_POOL_ID=your_identity_pool_id
   VITE_COGNITO_USER_POOL_ID=your_user_pool_id
   VITE_S3_BUCKET_NAME=your-bucket-name
   VITE_AWS_REGION=ap-northeast-1
   ```

3. **IAM 角色權限**：
   為 Identity Pool 的經過身份驗證的角色添加以下 S3 權限：
   ```json
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Action": ["s3:PutObject", "s3:PutObjectAcl"],
         "Resource": "arn:aws:s3:::your-bucket-name/*"
       }
     ]
   }
   ```

### 方法 2：使用 AWS 存取金鑰（不推薦用於生產環境）

只適用於開發環境，因為在前端暴露 AWS 憑證存在安全風險。

```bash
VITE_AWS_ACCESS_KEY_ID=your_access_key_id
VITE_AWS_SECRET_ACCESS_KEY=your_secret_access_key
VITE_S3_BUCKET_NAME=your-bucket-name
VITE_AWS_REGION=ap-northeast-1
```

## S3 Bucket 設定

1. **CORS 政策**（已存在於 S3_CORS_SETUP.md）：

   ```json
   [
     {
       "AllowedHeaders": ["*"],
       "AllowedMethods": ["GET", "PUT", "POST", "DELETE", "HEAD"],
       "AllowedOrigins": [
         "http://localhost:5173",
         "http://localhost:3000",
         "https://your-domain.com"
       ],
       "ExposeHeaders": ["ETag"],
       "MaxAgeSeconds": 3000
     }
   ]
   ```

2. **Bucket 政策**（可選，用於公開讀取）：
   ```json
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Sid": "PublicReadGetObject",
         "Effect": "Allow",
         "Principal": "*",
         "Action": "s3:GetObject",
         "Resource": "arn:aws:s3:::your-bucket-name/*"
       }
     ]
   }
   ```

## 程式碼變更

### 主要改進：

1. **使用 AWS SDK**：現在使用 `@aws-sdk/client-s3` 而不是直接的 fetch 請求
2. **適當的身份驗證**：支援 Cognito Identity Pool 或 AWS 存取金鑰
3. **錯誤處理**：更好的錯誤訊息和偵錯資訊
4. **檔案 metadata**：添加上傳時間、原始檔名等 metadata

### 新功能：

- 自動檢測可用的身份驗證方法
- 支援動態載入 AWS SDK 以減少打包大小
- 提供詳細的設定錯誤訊息
- 包含 ETag 在回應中

## 測試步驟

1. 確保所有必要的環境變數已設定
2. 確認 S3 bucket 的 CORS 和權限設定正確
3. 登入到應用程式
4. 嘗試上傳檔案
5. 檢查瀏覽器控制台是否有錯誤訊息
6. 驗證檔案是否成功上傳到 S3

## 疑難排解

### 常見錯誤：

1. **「AWS 憑證未設置」**：檢查環境變數是否正確設定
2. **「AccessDenied」**：檢查 IAM 權限和 S3 bucket 政策
3. **「CORS 錯誤」**：確認 S3 CORS 設定包含正確的來源 URL
4. **「Identity Pool 錯誤」**：確認 Identity Pool 與 User Pool 正確連結

### 偵錯提示：

- 檢查瀏覽器開發者工具的網路標籤
- 查看 AWS CloudTrail 日誌（如果啟用）
- 確認所有環境變數都有設定且格式正確
