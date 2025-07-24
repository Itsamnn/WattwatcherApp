@echo off
echo Building WattsWatcher App (Simple Build)...

set JAVA_HOME=F:\softwares\java 21
set PATH=%JAVA_HOME%\bin;%PATH%

echo Using Java version:
java -version

echo.
echo Starting build process...
gradlew.bat clean
gradlew.bat assembleDebug --no-daemon --offline

echo.
echo Build completed!
pause