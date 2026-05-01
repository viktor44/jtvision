setlocal

cd %~dp0..\examples

mvn compile exec:java -Dexec.mainClass="org.viktor44.jtvision.examples.tvhc.HelpCompiler" -Dexec.args="%*"

endlocal
