setlocal

cd %~dp0..

mvn compile exec:java -DskipTests -Dexec.mainClass="org.viktor44.jtvision.hello.HelloApp" -Dexec.args="%*"

rem cd %~dp0..
rem mvn test-compile exec:java -Dexec.mainClass="org.viktor44.jtvision.test.TestKeysApp" -Dexec.classpathScope="test"
rem mvn test-compile exec:java -Dexec.mainClass="org.viktor44.jtvision.test.TestAwtKeysApp" -Dexec.classpathScope="test"

endlocal