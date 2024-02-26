# PowerShell Script: cleanBuildTestAndRun.ps1

$env:MONGO_DB_URI = "mongodb://username:password@host:port/?ssl=true"
$env:VACCINATIONS_URL = "http://localhost:8081"

Write-Host "Environment Variables:"
Write-Host "MONGO_DB_URI=$env:MONGO_DB_URI"
Write-Host "VACCINATIONS_URL=$env:VACCINATIONS_URL"

Write-Host "Navigating to the microservice root directory:" $PSScriptRoot
Set-Location -Path $PSScriptRoot

Write-Host "Clear any pre-existing generated and compiled classes"
.\gradlew clean --no-daemon

Write-Host "Generate classes and compile"
.\gradlew build --no-daemon

Write-Host "Run all tests"
.\gradlew test --no-daemon

Write-Host "Start the microservice"
.\gradlew bootRun --no-daemon
