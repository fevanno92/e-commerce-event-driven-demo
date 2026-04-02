# Stage 1: Build
# Build context must be the project root: docker build -f order-service/Dockerfile .
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and parent pom.xml first for better caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy module pom.xml files for dependency resolution
COPY common/pom.xml common/pom.xml
COPY product-service/pom.xml product-service/pom.xml
COPY order-service/pom.xml order-service/pom.xml

# Download dependencies (cached if pom.xml unchanged)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B -pl common,order-service -am

# Copy source code
COPY common/src common/src
COPY order-service/src order-service/src

# Build common module and order-service
RUN ./mvnw clean package -DskipTests -B -pl common,order-service -am

# Stage 2: Runtime
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy JAR from builder stage
COPY --from=builder /app/order-service/target/*.jar app.jar

# Change ownership and switch to non-root user
RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
