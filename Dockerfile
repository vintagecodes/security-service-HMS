FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/Bootsecurity-1.0.0.jar Bootsecurity.jar
EXPOSE 8500
ENTRYPOINT ["java","-jar","Bootsecurity.jar"]
