@echo off
REM Build script for Encrypted Steganography Messenger (Windows)
setlocal

if not exist out mkdir out

echo Compiling...
javac -d out src\*.java
if errorlevel 1 (
  echo Compile failed.
  exit /b 1
)

echo Creating JAR...
jar cfm messenger.jar MANIFEST.MF -C out .
echo Done. Run:
echo   java -jar messenger.jar

endlocal
