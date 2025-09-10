# 使用官方 Node.js 20 作為基底
FROM node:20-alpine

# 設定工作目錄
WORKDIR /app

# 複製 package.json 和 package-lock.json（如果存在）
COPY package*.json ./

# 安裝依賴
RUN npm install

# 複製所有源代碼
COPY . .

# 暴露 Vite dev server 的端口
EXPOSE 5173

# 啟動開發服務器
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
