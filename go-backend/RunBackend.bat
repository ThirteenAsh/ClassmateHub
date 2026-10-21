@echo off
setlocal
cd /d "%~dp0"
where go >nul 2>nul
if %errorlevel% equ 0 (
    go run .
) else if exist ".tools\go\bin\go.exe" (
    set "GOCACHE=%~dp0.tools\build-cache"
    set "GOPATH=%~dp0.tools\gopath"
    ".tools\go\bin\go.exe" run .
) else (
    echo Please install Go 1.25 or later.
)
pause
