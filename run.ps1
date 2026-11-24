$env:JAVA_HOME = "C:\Users\Admin\jdk\jdk-21.0.5+11"
$env:M2_HOME = "C:\Users\Admin\maven\apache-maven-3.9.6"
$env:Path = "$env:JAVA_HOME\bin;$env:M2_HOME\bin;$env:Path"

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "       AeroCast - Pure Java Weather Intelligence" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "Starting AeroCast on http://localhost:8080 ..." -ForegroundColor Yellow
mvn spring-boot:run
