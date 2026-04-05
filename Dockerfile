# ---------- STAGE 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


# ---------- STAGE 2: Runtime ----------
FROM eclipse-temurin:17-jre-alpine

# 🔥 Librerías necesarias para JasperReports (Alpine)
RUN apk add --no-cache fontconfig freetype ttf-dejavu

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Xms64m", \
  "-Xmx256m", \
  "-XX:MaxMetaspaceSize=128m", \
  "-XX:+UseG1GC", \
  "-XX:+UseContainerSupport", \
  "-Djava.awt.headless=true", \
  "-jar", "app.jar"]
