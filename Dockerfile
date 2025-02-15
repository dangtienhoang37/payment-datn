## Build stage ##
FROM maven:3.9.9-amazoncorretto-17-alpine AS build

WORKDIR /app

# Sao chép file cấu hình Maven để cache dependency
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Sao chép source code
COPY src ./src
RUN mvn clean package -DskipTests=true && rm -rf ~/.m2/repository


## Run stage ##
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /run

# Sao chép file JAR từ build stage
COPY --from=build /app/target/payos-0.0.1-SNAPSHOT.jar /run/payos-0.0.1-SNAPSHOT.jar

# Mở cổng 8888
EXPOSE 8083

# Khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/run/payos-0.0.1-SNAPSHOT.jar"]
