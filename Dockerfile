
FROM openjdk:17-alpine

ARG JAR_FILE_PATH=build/libs/ClientViewServer-0.0.1-SNAPSHOT.jar

COPY $JAR_FILE_PATH ClientViewServer-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "ClientViewServer-0.0.1-SNAPSHOT.jar"]