#!/usr/bin/env bash
set -e

cd "$(dirname "$0")/.."

TARGET_DIR=examples/target

mvn -f "examples/pom.xml" \
  clean package \
  -DskipTests \
  --batch-mode --no-transfer-progress

echo === Building native classpath ===
CP=""
for jar in "$TARGET_DIR"/lib/*.jar; do
    CP="$CP:$jar"
done
CP="${CP:1}"

build_image() {
  local name=$1
  local main=$2
  echo "=== native-image: $main ==="
  "$JAVA_HOME/bin/native-image" \
      -cp "$CP" \
      --future-defaults=all \
      --no-fallback \
      -Os \
      -H:Name="$name" \
      -H:Path="$TARGET_DIR" \
      "$main"
}

build_image demo    org.viktor44.jtvision.examples.demo.DemoApp
build_image dir     org.viktor44.jtvision.examples.dir.DirApp
build_image editor  org.viktor44.jtvision.examples.editor.EditorApp
build_image forms   org.viktor44.jtvision.examples.forms.FormsApp
build_image mmenu   org.viktor44.jtvision.examples.mmenu.MultiMenuApp
build_image palette org.viktor44.jtvision.examples.palette.PaletteApp
