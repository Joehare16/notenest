
## STAGE 1 - Build the application JAR FILE

FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline
COPY src src
RUN ./mvnw package -DskipTests

## STAGE 2 - Run the application
FROM eclipse-temurin:17-jre
WORKDIR /app

## copy the jar file from the builder stage
COPY --from=build /app/target/*.jar app.jar

## run on port 8080
EXPOSE 8080

## Run the applicaiton when container starts
ENTRYPOINT ["java","-jar","app.jar"]
