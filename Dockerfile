
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Bước 2: Chạy ứng dụng
FROM openjdk:17-jdk-slim
WORKDIR /app
# Đổi cổng này nếu API của bạn không chạy ở 8080
EXPOSE 8080
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]