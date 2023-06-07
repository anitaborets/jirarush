FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/jira-1.0.jar
ADD ${JAR_FILE} jira-1.0.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/jira-1.0.jar"]