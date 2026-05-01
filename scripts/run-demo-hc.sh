cd "$(dirname "$0")/../examples"

mvn compile exec:java -Dexec.mainClass="org.viktor44.jtvision.examples.hc.HelpCompiler" -Dexec.args="%*"
