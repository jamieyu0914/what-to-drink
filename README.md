# what-to-drink

### CSS
We set the width of the Menu Modal to 80vw.  
The original size of the image is 2822×1588, and its aspect ratio is approximately 1.78:1 (2822 / 1588 ≈ 1.78).   
Therefore, the corresponding height of the image can be calculated as follows:
```
height = width / aspect ratio
height = 80vw / 1.78 ≈ 45vw
```

### Frontend Usage
1. You can use `npm run dev` to start the frontend server if you cd into the `frontend/` directory and have Node.js installed.
2. Open your web browser and navigate to `http://localhost:5173` to access the application.

### Backend API Usage
1. The backend API server runs on `http://localhost:8088/api`.
2. You can fetch a random drink suggestion by sending a GET request to `/api/drinks/auto-select` and so on.
3. `backend/target/demo-0.0.1-SNAPSHOT.jar` is the generated jar file after building the project, it's too large to be uploaded to GitHub, so you need to build it by yourself.
4. So, you can use `./mvnw clean package -DskipTests` to build the project if you have Java and Maven installed.

### Docker Commands
Make sure you have Docker and Docker Compose installed on your machine.
To build and run the application, use the following commands:
```
docker-compose up -d --build
docker-compose down
```

### Just want to know what to drink today. ヽ(灬º‿º灬)♡

```
    ⢸⣿
 ⢠⡞⢸⣿⠙⢶⡀
⢸⣁⣀⣀⣀⣀⣠⡞
 ⢸⣿⣿⣿⣿⣿⡿
 ⢸⣿⣿⣿⣿⣿⡿
 ⢸⣿ yoyo⣿
 ⢸⣿⣿⣿⣿⣿⡿
 ⢸⣿⣿⣿⣿⣿⣿
 ⠸⣿⣿⣿⣿⣿⡿
```

 © 2025 Jamie Yu. All Rights Reserved.