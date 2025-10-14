FROM openjdk:24-slim

WORKDIR /app

# Install required font libraries
RUN apt-get update && apt-get install -y --no-install-recommends \
    fontconfig \
    libfreetype6 \
    && rm -rf /var/lib/apt/lists/*

# Copy JAR
COPY target/properties-0.0.3-SNAPSHOT.jar app.jar

# Create output directory (optional, to match your app path)
RUN mkdir -p /app/output

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
