# Run Gradle clean build and block until completion
Write-Host "Running Gradle clean build..."
Start-Process "./gradlew" "clean" "build" -NoNewWindow -Wait
Write-Host "Gradle clean build completed."

# Function to wait until a file is updated
function Wait-ForFileUpdate
{
    param (
        [string]$filePath,
        [DateTime]$lastWriteTime
    )

    Write-Host "Waiting for file $filePath to be updated..."
    while ($true)
    {
        if (Test-Path $filePath)
        {
            if ((Get-Item $filePath).LastWriteTime -gt $lastWriteTime)
            {
                Write-Host "File $filePath has been updated."
                break
            }
        }
        Start-Sleep -Seconds 1
    }
}

# Function to get the initial last write time or current time if the file does not exist
function Get-InitialWriteTime
{
    param (
        [string]$filePath
    )

    if (Test-Path $filePath)
    {
        return (Get-Item $filePath).LastWriteTime
    }
    else
    {
        Write-Host "File $filePath does not exist. Using current time as reference."
        return Get-Date
    }
}

# Start the Wire Mock stub server in the background
Write-Host "Starting Wire Mock stub server..."
$wireMockProcess = Start-Process "./gradlew" "startWireMockEmbedded" -NoNewWindow -PassThru

# Path to the file to monitor
$filePath = "common/build/tmp/local/dynamicApplicationProperties.json"

# Get the last write time of the file before the process starts, or the current time if the file doesn't exist
$initialWriteTime = Get-InitialWriteTime -filePath $filePath

# Wait until the file is updated
Wait-ForFileUpdate -filePath $filePath -lastWriteTime $initialWriteTime

# Wait an additional 2 seconds after the file is updated
Start-Sleep -Seconds 2

# Start the in-memory MongoDB server
Write-Host "Starting in-memory MongoDB server..."
$mongoProcess = Start-Process "./gradlew" "startMongoDBEmbedded" -NoNewWindow -PassThru

# Get the last write time before the next step
$initialWriteTime = Get-InitialWriteTime -filePath $filePath
Wait-ForFileUpdate -filePath $filePath -lastWriteTime $initialWriteTime
Start-Sleep -Seconds 2

# Start the in-memory OAuth2 server
Write-Host "Starting in-memory OAuth2 server..."
$oauth2Process = Start-Process "./gradlew" "startOAuth2Embedded" -NoNewWindow -PassThru

# Get the last write time before the next step
$initialWriteTime = Get-InitialWriteTime -filePath $filePath
Wait-ForFileUpdate -filePath $filePath -lastWriteTime $initialWriteTime
Start-Sleep -Seconds 2

# Start the microservice with the local profile
Write-Host "Starting microservice with local profile..."
$microserviceProcess = Start-Process "./gradlew" "bootRun --args='--spring.profiles.active=local'" -NoNewWindow -PassThru

# Get the last write time before the next step
$initialWriteTime = Get-InitialWriteTime -filePath $filePath
Wait-ForFileUpdate -filePath $filePath -lastWriteTime $initialWriteTime
Start-Sleep -Seconds 2

# Execute the API tests
Write-Host "Executing API tests..."
Start-Process "./gradlew" ":api-test:run" -NoNewWindow -Wait
Write-Host "API tests completed."

# Execute the load tests
Write-Host "Executing load tests..."
Start-Process "./gradlew" ":load-test:run" -NoNewWindow -Wait
Write-Host "Load tests completed."

Write-Host "All tasks completed successfully."
