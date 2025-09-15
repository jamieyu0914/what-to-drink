# AWS Cognito 設定故障排除指南

## 錯誤：redirect_mismatch

### 問題描述
出現 `redirect_mismatch` 錯誤表示 AWS Cognito 用戶池中設定的回調 URL 與應用程式實際使用的 URL 不匹配。

### 解決步驟

#### 1. 檢查環境變數
確認 `.env` 文件中有以下設定：
```bash
VITE_COGNITO_DOMAIN=vocabulary.auth.us-east-1.amazoncognito.com
VITE_COGNITO_CLIENT_ID=3ad9f1p43v4gvsrniu41thd8c0
VITE_COGNITO_REDIRECT_URI=http://localhost:5173
```

#### 2. AWS Cognito 控制台設定

1. **登入 AWS 控制台**
   - 前往 AWS Cognito 服務
   - 選擇您的用戶池

2. **編輯應用程式客戶端**
   - 在左側導航欄中選擇「應用程式整合」
   - 找到您的應用程式客戶端（Client ID: `3ad9f1p43v4gvsrniu41thd8c0`）
   - 點擊「編輯」

3. **設定回調 URL**
   在「允許的回調 URL」中新增：
   ```
   http://localhost:5173
   http://localhost:5173/
   https://localhost:5173
   https://localhost:5173/
   ```

4. **設定登出 URL**
   在「允許的登出 URL」中新增：
   ```
   http://localhost:5173
   http://localhost:5173/
   https://localhost:5173
   https://localhost:5173/
   ```

5. **儲存變更**

#### 3. 生產環境設定
如果您要部署到生產環境，請也新增生產環境的 URL：
```
https://yourdomain.com
https://yourdomain.com/
```

### 調試技巧

1. **檢查瀏覽器控制台**
   - 開啟開發者工具
   - 檢查 Console 標籤中的錯誤訊息

2. **檢查網路請求**
   - 在 Network 標籤中查看失敗的請求
   - 檢查請求的 URL 和回應

3. **驗證設定**
   執行以下命令檢查環境變數：
   ```javascript
   console.log('Cognito 設定:', {
     domain: import.meta.env.VITE_COGNITO_DOMAIN,
     clientId: import.meta.env.VITE_COGNITO_CLIENT_ID,
     redirectUri: import.meta.env.VITE_COGNITO_REDIRECT_URI || window.location.origin
   })
   ```

### 常見問題

**Q: 為什麼需要多個回調 URL？**
A: 因為瀏覽器可能會使用不同的協議（http/https）或在 URL 後面加上斜線，設定多個變體可以確保兼容性。

**Q: 如何確認我的 Client ID 是否正確？**
A: 在 AWS Cognito 控制台的應用程式客戶端設定中可以找到正確的 Client ID。

**Q: 可以使用 IP 地址代替 localhost 嗎？**
A: 可以，但需要在 Cognito 設定中也加入對應的 IP 地址回調 URL。

### 聯繫支援
如果問題仍然存在，請提供以下資訊：
- 完整的錯誤訊息
- 瀏覽器控制台日誌
- 當前的環境變數設定（不包含敏感資訊）
- AWS Cognito 用戶池設定截圖
