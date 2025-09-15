# S3 CORS 設定指南

## 為了讓瀏覽器能夠直接上傳檔案到 S3，您需要設定 CORS 政策

### 1. 登入 AWS 控制台
- 前往 S3 服務
- 選擇您的儲存桶 (mynwss3bucket)

### 2. 設定 CORS 政策
在儲存桶的「權限」標籤中，找到「跨來源資源共用 (CORS)」並添加以下設定：

```json
[
    {
        "AllowedHeaders": [
            "*"
        ],
        "AllowedMethods": [
            "GET",
            "PUT",
            "POST",
            "DELETE",
            "HEAD"
        ],
        "AllowedOrigins": [
            "http://localhost:5173",
            "http://localhost:3000",
            "https://your-domain.com"
        ],
        "ExposeHeaders": [
            "ETag"
        ],
        "MaxAgeSeconds": 3000
    }
]
```

### 3. 儲存桶政策 (可選)
如果需要公開讀取權限，可以在「儲存桶政策」中添加：

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::mynwss3bucket/*"
        }
    ]
}
```

### 4. 重新啟動開發服務器
設定完成後，重新啟動 Vite 開發服務器。
