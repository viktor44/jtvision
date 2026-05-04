rem @echo off
setlocal enabledelayedexpansion

rem set GRAALVM25_HOME=D:\Java\graalvm-25
set JAVA_HOME=%GRAALVM25_HOME%
set SCRIPT_DIR=%~dp0
set EXAMPLES_DIR=%SCRIPT_DIR%..\examples
set TARGET_DIR=%EXAMPLES_DIR%\target

echo === Maven: package examples ===
call %MAVEN_HOME%\bin\mvn.cmd -f "%EXAMPLES_DIR%\pom.xml" clean package -DskipTests
if errorlevel 1 (
    echo Maven build failed
    exit /b 1
)

echo === Building native classpath ===
for %%f in ("%TARGET_DIR%\lib\*.jar") do (
    set CP=!CP!;%%f
)

echo === native-image: EditorApp ===
"%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -H:Name=editor ^
    -H:Path="%TARGET_DIR%" ^
    org.viktor44.jtvision.examples.editor.EditorApp

if errorlevel 1 (
    echo native-image build failed
    exit /b 1
)

echo === Done: %TARGET_DIR%\editor.exe ===
endlocal
