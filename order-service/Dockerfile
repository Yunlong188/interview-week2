FROM eclipse-temurin:17-jre-alpine

WORKDIR /var/usr/app

COPY target/week2-order-service-0.0.1-SNAPSHOT.jar order-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "order-service.jar"]