# Usamos una imagen de Maven para hacer build de proyecto con Java 17
# Llamaremos a este subentorno "build"
# Copiamos todo el contenido del repositorio
# Ejecutamos el comando mvn clean package (generará un archivo JAR para el despliegue)
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package

# Usamos una imagen de Openjdk 17
# Exponemos el puerto que nuestro componente va a usar para escuchar peticiones
# Copiamos desde "build" el JAR generado
# Marcamos el punto de arranque de la imagen con el comando "java -jar app.jar" que ejecutará nuestro componente
FROM openjdk:17
EXPOSE 8088
COPY --from=build /target/libros.elastic-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]