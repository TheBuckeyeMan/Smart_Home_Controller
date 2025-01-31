# Use an ARM64-compatible OpenJDK base image - EC@
FROM arm64v8/openjdk:17-jdk-slim

# Copy the built JAR file into the container
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Set the entry point
ENTRYPOINT ["java", "-jar", "app.jar"]
