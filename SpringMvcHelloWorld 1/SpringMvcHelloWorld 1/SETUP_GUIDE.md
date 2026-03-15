# Setup & Run Guide - Spring MVC Hello World

This guide explains how to set up and run the project on **Windows** and **macOS**.

---

## Prerequisites

You need two things installed before running the project:

### 1. Java 17 (or higher)

**Windows:**
1. Download from: https://adoptium.net/ (Eclipse Temurin JDK 17)
2. Run the installer - check "Set JAVA_HOME variable" during installation
3. Verify: open Command Prompt and run:
   ```
   java -version
   ```
   You should see something like: `openjdk version "17.x.x"`

**macOS:**
1. Install via Homebrew: `brew install openjdk@17`
2. Or download from: https://adoptium.net/
3. Verify: open Terminal and run: `java -version`

### 2. Apache Maven

**Windows:**
1. Download the **Binary zip archive** from: https://maven.apache.org/download.cgi
2. Extract it to `C:\Program Files\Apache\maven` (or any folder)
3. Add Maven to your PATH:
   - Open **System Properties** > **Environment Variables**
   - Under **System variables**, find `Path` and click **Edit**
   - Click **New** and add: `C:\Program Files\Apache\maven\bin`
   - Click OK to save
4. Verify: open a **new** Command Prompt and run:
   ```
   mvn -version
   ```

**macOS:**
1. Install via Homebrew: `brew install maven`
2. Verify: `mvn -version`

---

## Running the Project

### Windows

1. Open **Command Prompt** (cmd) or **PowerShell**
2. Navigate to the project folder:
   ```
   cd C:\path\to\SpringMvcHelloWorld
   ```
3. Run the batch script:
   ```
   run.bat
   ```
4. The script will:
   - Check that Java and Maven are installed
   - Build the project with Maven
   - Download Apache Tomcat 10.1.52 (first time only)
   - Deploy the WAR file to Tomcat
   - Start the Tomcat server
5. Open your browser and visit:
   ```
   http://localhost:8080/SpringMvcHelloWorld/hello
   http://localhost:8080/SpringMvcHelloWorld/hello?name=Abiral
   ```
6. To stop the server:
   ```
   run.bat stop
   ```
7. To rebuild and restart:
   ```
   run.bat restart
   ```

### macOS

1. Open **Terminal**
2. Navigate to the project folder:
   ```
   cd /path/to/SpringMvcHelloWorld
   ```
3. Make the script executable (first time only):
   ```
   chmod +x run.sh
   ```
4. Run it:
   ```
   ./run.sh
   ```
5. Open your browser and visit:
   ```
   http://localhost:8080/SpringMvcHelloWorld/hello
   http://localhost:8080/SpringMvcHelloWorld/hello?name=Abiral
   ```
6. To stop: `./run.sh stop`
7. To rebuild and restart: `./run.sh restart`

---

## Manual Setup (Without Script)

If the scripts don't work, you can do it manually:

### Step 1: Build the Project
```
cd /path/to/SpringMvcHelloWorld
mvn clean package
```
This creates `target/SpringMvcHelloWorld.war`.

### Step 2: Download Tomcat
1. Go to https://tomcat.apache.org/download-10.cgi
2. Download:
   - **Windows**: "64-bit Windows zip" under Binary Distributions > Core
   - **macOS**: "tar.gz" under Binary Distributions > Core
3. Extract it to any folder

### Step 3: Deploy the WAR
Copy the WAR file into Tomcat's webapps folder:
```
# Windows
copy target\SpringMvcHelloWorld.war C:\path\to\apache-tomcat-10.1.52\webapps\

# macOS
cp target/SpringMvcHelloWorld.war /path/to/apache-tomcat-10.1.52/webapps/
```

### Step 4: Start Tomcat
```
# Windows
C:\path\to\apache-tomcat-10.1.52\bin\catalina.bat start

# macOS
/path/to/apache-tomcat-10.1.52/bin/catalina.sh start
```

### Step 5: Open in Browser
```
http://localhost:8080/SpringMvcHelloWorld/hello
```

### Step 6: Stop Tomcat
```
# Windows
C:\path\to\apache-tomcat-10.1.52\bin\catalina.bat stop

# macOS
/path/to/apache-tomcat-10.1.52/bin/catalina.sh stop
```

---

## Troubleshooting

### "java is not recognized" / "java: command not found"
- Java is not installed or not in PATH
- Windows: Reinstall Java and check "Set JAVA_HOME" option
- macOS: Run `brew install openjdk@17`

### "mvn is not recognized" / "mvn: command not found"
- Maven is not installed or not in PATH
- Windows: Add Maven's `bin` folder to your system PATH
- macOS: Run `brew install maven`

### Port 8080 already in use
- Another app is using port 8080
- Find and stop it:
  - Windows: `netstat -aon | findstr :8080` then `taskkill /PID <pid> /F`
  - macOS: `lsof -i :8080` then `kill <pid>`

### 404 Not Found
- Make sure the URL includes the app name: `/SpringMvcHelloWorld/hello` (not just `/hello`)
- Check Tomcat logs for errors:
  - Windows: `apache-tomcat-10.1.52\logs\catalina.out`
  - macOS: `apache-tomcat-10.1.52/logs/catalina.out`

### BUILD FAILURE
- Make sure you are in the project root directory (where `pom.xml` is)
- Make sure you have internet access (Maven downloads dependencies)

---

## Project Structure

```
SpringMvcHelloWorld/
├── pom.xml                                  <-- Maven config (dependencies)
├── run.sh                                   <-- macOS run script
├── run.bat                                  <-- Windows run script
├── SETUP_GUIDE.md                           <-- This file
├── Spring_MVC_Guide.md                      <-- Spring MVC concepts explained
└── src/main/
    ├── java/com/example/
    │   └── controller/
    │       └── HelloController.java         <-- Controller (handles requests)
    └── webapp/WEB-INF/
        ├── web.xml                          <-- Registers DispatcherServlet
        ├── dispatcher-servlet.xml           <-- Spring MVC configuration
        └── views/
            └── hello.jsp                    <-- View (renders HTML)
```
