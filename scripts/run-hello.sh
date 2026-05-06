cd "$(dirname "$0")/.."

#mvn exec:java -Dexec.mainClass="org.viktor44.jtvision.hello.HelloApp"

mvn clean package exec:java -DskipTests -Dexec.mainClass="org.viktor44.jtvision.test.TestKeysApp" -Dexec.classpathScope="test"
