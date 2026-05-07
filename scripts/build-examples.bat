/@echo off
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
"%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -H:Name=demo ^
    -H:Path="%TARGET_DIR%" ^
    org.viktor44.jtvision.examples.demo.DemoApp
if errorlevel 1 (
  echo native-image build failed
  exit /b 1
)

echo === native-image: DirApp ===
"%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -H:Name=dir ^
    -H:Path="%TARGET_DIR%" ^
    org.viktor44.jtvision.examples.dir.DirApp
if errorlevel 1 (
  echo native-image build failed
  exit /b 1
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

echo === native-image: FormsApp ===
"%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -H:Name=forms ^
    -H:Path="%TARGET_DIR%" ^
    org.viktor44.jtvision.examples.forms.FormsApp
if errorlevel 1 (
  echo native-image build failed
  exit /b 1
)

echo === native-image: MultiMenuApp ===
"%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -H:Name=mmenu ^
    -H:Path="%TARGET_DIR%" ^
    org.viktor44.jtvision.examples.mmenu.MultiMenuApp
if errorlevel 1 (
  echo native-image build failed
  exit /b 1
)

echo === native-image: PaletteApp ===
"%JAVA_HOME%\bin\native-image" ^
    -cp "!CP!" ^
    --future-defaults=all ^
    --no-fallback ^
    -Os ^
    -H:Name=palette ^
    -H:Path="%TARGET_DIR%" ^
    org.viktor44.jtvision.examples.palette.PaletteApp
if errorlevel 1 (
  echo native-image build failed
  exit /b 1
)
