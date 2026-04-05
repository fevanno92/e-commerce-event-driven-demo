# Stage 1: Build
# Build context must be the project root: docker build -f payment-service/Dockerfile .
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and parent pom.xml first for better caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy module pom.xml files (ONLY those needed for this service)
COPY common/pom.xml common/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml

# Remove other modules from the parent pom.xml to avoid errors during build
RUN sed -i 's|<module>product-service</module>||' pom.xml && \
    sed -i 's|<module>order-service</module>||' pom.xml
    sed -i 's|<module>stock-service</module>||' pom.xml

# Download dependencies with SHARED cache mount
RUN --mount=type=cache,id=maven-cache,target=/root/.m2 \
    chmod +x mvnw && ./mvnw dependency:go-offline -B -pl common,payment-service -am

# Copy source code
COPY common/src common/src
COPY payment-service/src payment-service/src

# Build with SHARED cache mount
RUN --mount=type=cache,id=maven-cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests -B -pl common,payment-service -am

# Stage 2: Runtime
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy JAR from builder stage
COPY --from=builder /app/payment-service/target/*.jar app.jar

# Change ownership and switch to non-root user
RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
