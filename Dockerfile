# Stage 1: Build the application
FROM maven:3.8.6-openjdk-21 AS build
# Thiết lập thư mục làm việc
WORKDIR /app
# Sao chép pom.xml và tải xuống các phụ thuộc Maven
COPY pom.xml .
RUN mvn dependency:go-offline
# Sao chép toàn bộ mã nguồn vào thư mục làm việc
COPY src ./src
# Build ứng dụng và tạo file jar
RUN mvn clean package -DskipTests
# Stage 2: Run the application
FROM openjdk:17-jdk-alpine
# Thiết lập thư mục làm việc cho ứng dụng
WORKDIR /app
# Sao chép file jar từ stage build
COPY --from=build /app/target/*.jar app.jar
# Đặt biến môi trường (nếu cần)
ENV SPRING_PROFILES_ACTIVE=prod
# Mở cổng mà ứng dụng sẽ chạy (thay đổi nếu ứng dụng của bạn sử dụng cổng khác)
EXPOSE 8080
# Lệnh để chạy ứng dụng
ENTRYPOINT ["java","-jar","app.jar"]
