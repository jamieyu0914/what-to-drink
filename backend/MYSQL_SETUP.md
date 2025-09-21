# MySQL 数据库设置指南

## 前提条件

确保您的系统已安装 MySQL。如果尚未安装，请按照以下步骤操作：

### macOS 安装 MySQL

1. 使用 Homebrew 安装：

```bash
brew install mysql
```

2. 启动 MySQL 服务：

```bash
brew services start mysql
```

3. 运行安全设置脚本（可选）：

```bash
mysql_secure_installation
```

## 数据库设置

1. 登录 MySQL：

```bash
mysql -u root -p
```

2. 创建数据库：

```sql
CREATE DATABASE what_to_drink CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 创建用户（可选，如果不想使用 root）：

```sql
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON what_to_drink.* TO 'app_user'@'localhost';
FLUSH PRIVILEGES;
```

4. 退出 MySQL：

```sql
exit
```

## 应用程序配置

### 默认配置

应用程序已配置为使用以下默认设置：

- 数据库 URL: `jdbc:mysql://localhost:3306/what_to_drink`
- 用户名: `root`
- 密码: `password`

### 自定义配置

如果您需要修改数据库连接设置，可以通过环境变量来覆盖默认值：

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/your_database_name
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
```

或者在运行应用程序时传递参数：

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name \
  --spring.datasource.username=your_username \
  --spring.datasource.password=your_password
```

## 启动应用程序

1. 确保 MySQL 服务正在运行：

```bash
brew services list | grep mysql
```

2. 构建并运行应用程序：

```bash
./mvnw clean install
./mvnw spring-boot:run
```

## 验证连接

应用程序启动后，您应该在日志中看到类似以下的信息：

```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

## 故障排除

### 常见问题

1. **连接被拒绝**：

   - 检查 MySQL 服务是否正在运行
   - 验证端口 3306 是否可访问

2. **认证失败**：

   - 检查用户名和密码是否正确
   - 确保用户具有适当的权限

3. **数据库不存在**：
   - 确保已创建 `what_to_drink` 数据库
   - 检查数据库名称拼写

### 重置数据库

如果需要重置数据库：

```sql
DROP DATABASE what_to_drink;
CREATE DATABASE what_to_drink CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

应用程序将在下次启动时自动重新创建表结构。
