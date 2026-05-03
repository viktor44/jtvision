setlocal

chcp 65001
cd %~dp0..
mvn versions:set versions:commit -DnewVersion=%1

endlocal
