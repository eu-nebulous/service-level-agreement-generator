FROM maven:3.9.5-eclipse-temurin

WORKDIR /sla-generator

COPY src/ src/
COPY pom.xml .

RUN mvn package -Dmaven.test.skip
# RUN mv target/nebulous-sla-0.0.1-SNAPSHOT.jar nebulous-sla-0.0.1-SNAPSHOT.jar
WORKDIR /sla-generator/target

CMD ["sudo", "java", "-jar", "nebulous-sla-0.0.1-SNAPSHOT.jar", "http://localhost:80"]
RUN ls
EXPOSE 8081
