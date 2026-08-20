# Use Eclipse Temurin JDK 17
FROM eclipse-temurin:17-jdk-alpine AS build

# Install Gradle
RUN apk add --no-cache gradle

# Set working directory
WORKDIR /app

# Copy Gradle files
COPY build.gradle settings.gradle gradle.properties ./
COPY src ./src

# Build the application
RUN gradle shadowJar --no-daemon

# Production stage
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/build/libs/salary-management-backend-0.1-all.jar ./app.jar

# Create data directory for SQLite
RUN mkdir -p /app/data

# Expose port
EXPOSE 10000

# Set environment variables
ENV MICRONAUT_SERVER_PORT=10000
ENV MICRONAUT_ENVIRONMENTS=production
ENV MICRONAUT_SERVER_CORS_ENABLED=true
ENV MICRONAUT_SERVER_CORS_CONFIGURATIONS_WEB_ALLOWED_ORIGINS=https://salary-management-system-steel.vercel.app

# Run the application
CMD ["java", "-jar", "app.jar"]
