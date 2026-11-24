@echo off
set "JAVA_HOME=C:\Users\Admin\jdk\jdk-21.0.5+11"
set "M2_HOME=C:\Users\Admin\maven\apache-maven-3.9.6"
set "PATH=%JAVA_HOME%\bin;%M2_HOME%\bin;%PATH%"

echo ========================================================
echo        AeroCast - Pure Java Weather Intelligence
echo ========================================================
echo Using Java 21:
java -version
echo.
echo Starting AeroCast Spring Boot Application at http://localhost:8080 ...
mvn spring-boot:run
pause
