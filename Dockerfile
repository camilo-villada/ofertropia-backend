# Dockerfile para Spring Boot (Java 21)
# 1. Usa una imagen base oficial de Java 21
FROM eclipse-temurin:21-jre-alpine

# 2. Establece el directorio de trabajo
WORKDIR /app

# 3. Copia el JAR generado por Maven al contenedor
COPY target/ofertropia-backend-0.0.1-SNAPSHOT.jar app.jar

# 4. Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# 5. Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
