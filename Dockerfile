FROM maven:3.9.5-eclipse-temurin

WORKDIR /sla-generator

COPY src/ src/
COPY pom.xml .

RUN mvn package -Dmaven.test.skip

WORKDIR /sla-generator/target


CMD ["java", "-jar", "nebulous-ont-0.0.1-SNAPSHOT.jar", "http://localhost:80"]

EXPOSE 8081
