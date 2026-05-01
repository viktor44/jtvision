#cd "$(dirname "$0")/.."

#mvn exec:java -Dexec.mainClass="org.viktor44.jtvision.hello.HelloApp"

cd "$(dirname "$0")/../examples"
mvn compile exec:java -Dexec.mainClass="org.viktor44.jtvision.examples.test.TestKeysApp"
