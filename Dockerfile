FROM ubuntu:latest
LABEL authors="DELL"

ENTRYPOINT ["top", "-b"]

# Sử dụng hình ảnh OpenJDK chính thức
FROM openjdk:17-jdk-alpine

# Tạo thư mục để chứa ứng dụng
VOLUME /tmp

# Sao chép file JAR của ứng dụng vào container
COPY target/GameServer-0.0.1-SNAPSHOT.jar app.jar

# Chạy ứng dụng
ENTRYPOINT ["java","-jar","/app.jar"]
