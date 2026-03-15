#!/bin/bash

# ============================================================
# run.sh - Build, Deploy, and Start Tomcat for Spring MVC app
# ============================================================
# Usage:
#   ./run.sh          Build + Start Tomcat
#   ./run.sh stop     Stop Tomcat
#   ./run.sh restart   Rebuild + Restart Tomcat
# ============================================================

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
TOMCAT_VERSION="10.1.52"
TOMCAT_DIR="$PROJECT_DIR/apache-tomcat-$TOMCAT_VERSION"
TOMCAT_URL="https://dlcdn.apache.org/tomcat/tomcat-10/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz"
WAR_NAME="SpringMvcHelloWorld"

# Stop Tomcat
stop_tomcat() {
    if [ -f "$TOMCAT_DIR/bin/catalina.sh" ]; then
        echo "Stopping Tomcat..."
        "$TOMCAT_DIR/bin/catalina.sh" stop 2>/dev/null
        sleep 2
    fi
}

# Handle 'stop' command
if [ "$1" = "stop" ]; then
    stop_tomcat
    echo "Tomcat stopped."
    exit 0
fi

# Handle 'restart' command
if [ "$1" = "restart" ]; then
    stop_tomcat
fi

# Step 1: Build the project with Maven
echo "========================================="
echo " Step 1: Building the project with Maven"
echo "========================================="
cd "$PROJECT_DIR"
mvn clean package -q
if [ $? -ne 0 ]; then
    echo "BUILD FAILED. Fix the errors and try again."
    exit 1
fi
echo "Build successful: target/$WAR_NAME.war"

# Step 2: Download Tomcat if not present
if [ ! -d "$TOMCAT_DIR" ]; then
    echo ""
    echo "========================================="
    echo " Step 2: Downloading Apache Tomcat $TOMCAT_VERSION"
    echo "========================================="
    cd "$PROJECT_DIR"
    curl -L -o tomcat.tar.gz "$TOMCAT_URL"
    if [ $? -ne 0 ]; then
        echo "Failed to download Tomcat. Check your internet connection."
        rm -f tomcat.tar.gz
        exit 1
    fi
    tar -xzf tomcat.tar.gz
    rm tomcat.tar.gz
    chmod +x "$TOMCAT_DIR/bin/"*.sh
    echo "Tomcat downloaded to: $TOMCAT_DIR"
else
    echo ""
    echo "Tomcat already exists at: $TOMCAT_DIR"
fi

# Step 3: Stop any running Tomcat instance
stop_tomcat

# Step 4: Deploy the WAR file
echo ""
echo "========================================="
echo " Step 3: Deploying WAR to Tomcat"
echo "========================================="
rm -rf "$TOMCAT_DIR/webapps/$WAR_NAME" "$TOMCAT_DIR/webapps/$WAR_NAME.war"
cp "$PROJECT_DIR/target/$WAR_NAME.war" "$TOMCAT_DIR/webapps/"
echo "Deployed $WAR_NAME.war to Tomcat webapps/"

# Step 5: Start Tomcat
echo ""
echo "========================================="
echo " Step 4: Starting Tomcat"
echo "========================================="
"$TOMCAT_DIR/bin/catalina.sh" start

echo ""
echo "========================================="
echo " Tomcat is starting..."
echo " App URL: http://localhost:8080/$WAR_NAME/hello"
echo " Try:     http://localhost:8080/$WAR_NAME/hello?name=Abiral"
echo " Stop:    ./run.sh stop"
echo "========================================="
