@echo off
setlocal
set "APP_HOME=%~dp0"
set "GRADLE_VERSION=9.7.1"
if not defined GRADLE_USER_HOME set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "CACHE_DIR=%GRADLE_USER_HOME%\vietsub-distributions\gradle-%GRADLE_VERSION%"
set "DIST_DIR=%CACHE_DIR%\gradle-%GRADLE_VERSION%"
if exist "%DIST_DIR%\bin\gradle.bat" goto run
if not exist "%CACHE_DIR%" mkdir "%CACHE_DIR%"
set "ZIP=%CACHE_DIR%\gradle-%GRADLE_VERSION%-bin.zip"
echo Bootstrapping Gradle %GRADLE_VERSION%...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ZIP%'"
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '%ZIP%' '%CACHE_DIR%'"
del /q "%ZIP%"
:run
call "%DIST_DIR%\bin\gradle.bat" -p "%APP_HOME%" %*
endlocal
