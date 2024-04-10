# Base image
FROM adoptopenjdk/openjdk17:alpine

# Set working directory
WORKDIR /app

# Copy the jar file into the container
COPY build/libs/ledger-command-service-0.0.1-SNAPSHOT.jar /app/ledger-command-service-0.0.1-SNAPSHOT.jar

# Install curl and other necessary tools
RUN apk add --no-cache curl bash

# Expose ports
EXPOSE 8080

# Command to run the service
CMD ["java", "-jar", "/app/ledger-command-service-0.0.1-SNAPSHOT.jar"]
