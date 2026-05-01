setlocal

cd %~dp0..

mvn compile exec:java -DskipTests -Dexec.mainClass="org.viktor44.jtvision.hello.HelloApp" -Dexec.args="%*"

rem cd %~dp0..\examples
rem mvn compile exec:java -Dexec.mainClass="org.viktor44.jtvision.examples.test.TestKeysApp"

endlocal