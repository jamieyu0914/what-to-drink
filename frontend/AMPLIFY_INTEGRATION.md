# AWS Amplify 整合指南

本專案已整合 AWS Amplify，支援使用 Cognito token 進行 S3 檔案上傳。

## 功能特色

1. **自動登入檢測**: 系統會優先檢查 Amplify 配置，如果未配置則回退到傳統 Cognito 方式
2. **安全的 S3 上傳**: 使用 Amplify Storage 和用戶 Cognito token 進行檔案上傳
3. **用戶隔離**: 每個用戶的檔案會存放在獨立的資料夾中 (`uploads/{user_id}/`)
4. **多種上傳方式**: 支援 Amplify Storage、直接 S3 上傳和後端代理上傳

## 設定步驟

### 1. 環境變數配置

複製 `.env.example` 為 `.env.local` 並設定以下變數：

```bash
# 必要的 Amplify 配置
VITE_AWS_REGION=your-aws-region
VITE_COGNITO_USER_POOL_ID=your_user_pool_id
VITE_COGNITO_CLIENT_ID=your_cognito_client_id
VITE_COGNITO_DOMAIN=your-cognito-domain.auth.region.amazoncognito.com
VITE_S3_BUCKET_NAME=your-s3-bucket-name

# 可選配置
VITE_COGNITO_REDIRECT_URI=http://localhost:5173
```

### 2. AWS Cognito User Pool 設定

確保您的 Cognito User Pool 具有以下設定：

1. **App Client 設定**：
   - 啟用 "Use Cognito Hosted UI"
   - 設定 Callback URLs: `http://localhost:5173` (或您的域名)
   - 設定 Sign out URLs: `http://localhost:5173`
   - 允許的 OAuth 流程：Authorization code grant
   - 允許的 OAuth 範圍：email, openid, aws.cognito.signin.user.admin, profile

2. **Identity Pool 設定**（如果使用）：
   - 建立一個 Identity Pool
   - 設定 Cognito User Pool 作為身份提供者
   - 配置 IAM 角色以允許 S3 存取

### 3. S3 Bucket 權限設定

確保您的 S3 bucket 具有適當的 CORS 和 IAM 政策：

#### CORS 配置
```json
[
    {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
        "AllowedOrigins": ["http://localhost:5173", "https://your-domain.com"],
        "ExposeHeaders": ["ETag"]
    }
]
```

#### IAM 政策範例
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:DeleteObject"
            ],
            "Resource": "arn:aws:s3:::your-bucket-name/uploads/${cognito-identity.amazonaws.com:sub}/*"
        },
        {
            "Effect": "Allow",
            "Action": "s3:ListBucket",
            "Resource": "arn:aws:s3:::your-bucket-name",
            "Condition": {
                "StringLike": {
                    "s3:prefix": ["uploads/${cognito-identity.amazonaws.com:sub}/*"]
                }
            }
        }
    ]
}
```

## 使用方式

### 登入流程

1. 點擊 "Cognito 登入" 按鈕
2. 如果配置了 Amplify，會顯示 "AWS Amplify 登入" 選項
3. 使用 Amplify 登入會重定向到 Cognito Hosted UI
4. 登入成功後會回到應用程式並顯示用戶資訊

### 檔案上傳

1. 使用 Amplify 登入後，點擊 "上傳檔案"
2. 選擇檔案，系統會自動使用 Amplify Storage 上傳
3. 檔案會存放在 `uploads/{user_id}/` 路徑下
4. 上傳成功後會顯示確認訊息

### 檔案管理

- 點擊 "檢視上傳檔案" 可以查看已上傳的檔案列表
- 每個檔案記錄包含檔案名稱、S3 位置、上傳時間等資訊

## 開發注意事項

1. **環境變數**: 確保所有必要的環境變數都已正確設定
2. **HTTPS**: 在生產環境中務必使用 HTTPS
3. **錯誤處理**: 系統包含完整的錯誤處理和回退機制
4. **安全性**: 所有檔案上傳都基於用戶的 Cognito token，確保安全隔離

## 疑難排解

### 常見問題

1. **"Amplify 未正確設定"**
   - 檢查 `.env.local` 中的環境變數
   - 確認 `VITE_COGNITO_USER_POOL_ID` 已設定

2. **登入後重定向失敗**
   - 檢查 Cognito App Client 的 Callback URLs 設定
   - 確認 `VITE_COGNITO_REDIRECT_URI` 與 Cognito 設定一致

3. **檔案上傳失敗**
   - 檢查 S3 bucket 的 CORS 設定
   - 確認 IAM 角色具有適當的 S3 權限
   - 檢查用戶是否已使用 Amplify 登入

4. **Token 相關錯誤**
   - 確認 Identity Pool 已正確設定
   - 檢查 IAM 角色政策是否允許所需的 S3 操作

## API 參考

### AmplifyAuth 類別方法

- `isConfigured()`: 檢查 Amplify 是否已配置
- `signInWithHostedUI()`: 使用 Hosted UI 登入
- `signOut()`: 登出
- `getCurrentUser()`: 獲取當前用戶資訊
- `isLoggedIn()`: 檢查登入狀態
- `getIdToken()`: 獲取 ID Token
- `getAccessToken()`: 獲取 Access Token
- `uploadToS3(file, key)`: 上傳檔案到 S3
- `listUserFiles(prefix)`: 列出用戶檔案
- `deleteFromS3(key)`: 刪除 S3 檔案
