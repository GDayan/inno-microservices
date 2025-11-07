FROM eclipse-temurin:21-jdk

ARG JAR_FILE=target/userService-1.0-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]