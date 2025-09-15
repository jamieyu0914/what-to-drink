# AWS Cognito 整合說明

## 功能改進

已將原本的佔位符 Cognito 登入功能升級為完整的 AWS Cognito 整合方案。

## 主要改進

### 1. 完整的 Cognito 工具類 (`src/utils/cognito.js`)

- 封裝了 Cognito 的登入、登出邏輯
- 提供 token 和用戶狀態管理
- 處理 OAuth 回調流程

### 2. 動態用戶界面

- 按鈕文字根據登入狀態變化（"Cognito 登入" / "登出"）
- 按鈕樣式根據狀態變化（橙色 / 紅色）
- 自動處理登入回調

### 3. 環境變數配置

- 支援透過 `.env.local` 文件配置 Cognito 設定
- 提供 `.env.local.example` 作為設定範本

## 設定步驟

### 1. 複製環境變數範本

```bash
cp .env.local.example .env.local
```

### 2. 編輯 `.env.local` 文件

```env
VITE_COGNITO_DOMAIN=your-cognito-domain.auth.region.amazoncognito.com
VITE_COGNITO_CLIENT_ID=your-cognito-client-id
VITE_COGNITO_REDIRECT_URI=http://localhost:5173
```

### 3. 重新啟動開發伺服器

```bash
npm run dev
```

## 使用方式

### 登入流程

1. 用戶點擊 "Cognito 登入" 按鈕
2. 自動重定向到 AWS Cognito 登入頁面
3. 用戶完成登入後，重定向回應用
4. 自動處理授權碼並更新登入狀態

### 登出流程

1. 已登入用戶點擊 "登出" 按鈕
2. 確認登出對話框
3. 清除本地用戶資料並重定向到 Cognito 登出頁面

## 技術特色

- **無需額外依賴**：使用原生 Web API 實現 OAuth 2.0 流程
- **狀態管理**：自動管理登入狀態和用戶資訊
- **錯誤處理**：完善的錯誤處理和用戶提示
- **開發友好**：詳細的 console 日誌和錯誤訊息

## 後續擴展

如需要更複雜的功能，可以考慮整合 AWS Amplify SDK：

```bash
npm install aws-amplify
```

但目前的實現已經能夠滿足基本的 Cognito 登入需求。
