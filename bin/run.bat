@echo off
if "%OS%" == "Windows_NT" setlocal

CD ..
SET CURRENT_PATH=%CD%
SET LIB_PATH=%CURRENT_PATH%\libs
SET clspath=%CURRENT_PATH%\conf

set cp= 
for %%i in (".\libs\*.jar") do call .\bin\setenv.bat %%i 

set J_OPTS=-Dfile.encoding=UTF-8
rem set J_OPTS=-noverify -javaagent:%CURRENT_PATH%\ext\jrebel.jar
rem SET JAVA_CMD=%CURRENT_PATH%/jre/bin/javaw -Xms512M -Xmx1024M -Dfile.encoding=UTF-8 -Djava.ext.dirs="%LIB_PATH%"
rem -Dfile.encoding=UTF-8  -cp %clspath% -Djava.ext.dirs="%LIB_PATH%"
SET JAVA_CMD=java -cp %cp%;%clspath% -Xms128m -Xmx1024m -Xmn42m -XX:PermSize=64m -XX:MaxPermSize=128m %J_OPTS% -Dappname="ICloudUnion" -DbaseHome=%CURRENT_PATH%
rem start "ICloudUnion" %JAVA_CMD% com.szl.icu.miner.rest.HttpBoot
start "ICloudUnion" %JAVA_CMD% com.szl.icu.miner.rest.HttpBoot

rem pause