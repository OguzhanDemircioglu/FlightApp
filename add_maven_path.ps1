$mavenBin = "C:\apache-maven-3.9.12\bin"
$currentPath = [System.Environment]::GetEnvironmentVariable("Path", "User")
if ($currentPath -notlike "*apache-maven*") {
    [System.Environment]::SetEnvironmentVariable("Path", "$currentPath;$mavenBin", "User")
    Write-Host "Maven bin added to PATH"
} else {
    Write-Host "Maven already in PATH"
}
