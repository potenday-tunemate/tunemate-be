FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
COPY src src

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=builder /workspace/build/libs/be-0.0.1-SNAPSHOT.jar be.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "be.jar"]
