# Build stage

# FROM maven:3.8.5-openjdk-17 AS build
# FROM 906330555725.dkr.ecr.eu-central-1.amazonaws.com/maven:3.8.5-openjdk-17 as build

FROM public.ecr.aws/docker/library/maven:3.8.5-openjdk-17 as build

WORKDIR /app
# Copy application source code and build application dependencies

COPY . .
RUN mvn clean package -Dmaven.test.skip=true

# Final stage
# FROM amazoncorretto:17
# FROM 906330555725.dkr.ecr.eu-central-1.amazonaws.com/amazoncorretto:17
FROM public.ecr.aws/amazoncorretto/amazoncorretto:17

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/propass-monolith-api/target/propass-monolith-api-0.0.1-SNAPSHOT.jar .

# Set the entry point to run the JAR file
CMD ["java", "-jar", "propass-monolith-api-0.0.1-SNAPSHOT.jar"]