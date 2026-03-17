
FROM maven:3.9-amazoncorretto-25 AS builder

WORKDIR /app

COPY ./pom.xml .
COPY ./src ./src

RUN mvn clean package -DskipTests

FROM amazoncorretto:25

WORKDIR /app

COPY --from=builder /app/target/*.jar simulator.jar

COPY ./src/main/resources/sensor.csv /app/sensor.csv

CMD ["java", "-jar", "simulator.jar"]