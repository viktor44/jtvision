@echo off
setlocal enabledelayedexpansion

cd %~dp0..

set TARGET_DIR=examples\target
call mvn.cmd -f "examples\pom.xml" ^
  clean package ^
  -DskipTests ^
  --batch-mode --no-transfer-progress

echo === Building native classpath ===
for %%f in ("%TARGET_DIR%\lib\*.jar") do (
    set CP=!CP!;%%f
)

echo === native-image: DemoApp ===
call "%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -o "%TARGET_DIR%\demo" ^
    org.viktor44.jtvision.examples.demo.DemoApp

if errorlevel 1 (
  echo native-image build failed
  exit /b 1
)

echo === native-image: DirApp ===
call "%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -o "%TARGET_DIR%\dir" ^
    org.viktor44.jtvision.examples.dir.DirApp

if errorlevel 1 (
  echo native-image build failed
  exit /b 1
)

echo === native-image: EditorApp ===
call "%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -o "%TARGET_DIR%\editor" ^
    org.viktor44.jtvision.examples.editor.EditorApp
if errorlevel 1 (
  echo native-image build failed
  exit /b 1
)

echo === native-image: FormsApp ===
call "%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -o "%TARGET_DIR%\forms" ^
    org.viktor44.jtvision.examples.forms.FormsApp
if errorlevel 1 (
  echo native-image build failed
  exit /b 1
)

echo === native-image: MultiMenuApp ===
call "%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -o "%TARGET_DIR%\mmenu" ^
    org.viktor44.jtvision.examples.mmenu.MultiMenuApp
if errorlevel 1 (
  echo native-image build failed
  exit /b 1
)

echo === native-image: PaletteApp ===
call "%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -o "%TARGET_DIR%\palette" ^
    org.viktor44.jtvision.examples.palette.PaletteApp
if errorlevel 1 (
  echo native-image build failed
  exit /b 1
)
