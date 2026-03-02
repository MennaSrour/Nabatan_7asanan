# PowerShell script to fix Kotlin daemon build error

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "  Fixing Kotlin Daemon Build Error" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

Set-Location "D:\the chance\AndroidCompose\Nabata_7asena"

Write-Host "Step 1: Stopping all Gradle daemons..." -ForegroundColor Yellow
& .\gradlew --stop
Start-Sleep -Seconds 2
Write-Host ""

Write-Host "Step 2: Cleaning build directories..." -ForegroundColor Yellow
Remove-Item -Recurse -Force .gradle -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force build -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force app\build -ErrorAction SilentlyContinue
Write-Host "Build directories cleaned!" -ForegroundColor Green
Write-Host ""

Write-Host "Step 3: Running Gradle clean..." -ForegroundColor Yellow
& .\gradlew clean --no-daemon
Write-Host ""

Write-Host "Step 4: Building project with fresh dependencies..." -ForegroundColor Yellow
& .\gradlew assembleDebug --no-daemon --refresh-dependencies
Write-Host ""

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "  Build process completed!" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

