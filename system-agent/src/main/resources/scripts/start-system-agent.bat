@echo off

REM if "%SYSTEM_AGENT_HOME%" == "" 
REM echo The environment variable SYSTEM_AGENT_HOME does not exist, please add it before going further. It should be the directory where System Agent is installed
REM EXIT /B 1

call %SYSTEM_AGENT_HOME%\settings.bat

SET APPLICATION_PROPERTIES=%SYSTEM_AGENT_HOME%\conf\application.properties
SET SERVICES_PROPERTIES=%SYSTEM_AGENT_HOME%\resources\services.properties
SET EXT_LIBS_HOME=%SYSTEM_AGENT_HOME%\ext-lib
SET IKR_LIBS_HOME=%SYSTEM_AGENT_HOME%\ikr-lib
SET SIGAR_HOME=%SYSTEM_AGENT_HOME%\resources\sigar-1.6.3.0
SET LOG_CONFIG=%SYSTEM_AGENT_HOME%\conf\log4j.xml
SET JAVA_CMD=%JAVA_HOME%\bin\java

SET IKR_JAVA_ARGS=-Djava.rmi.server.hostname=%HOSTNAME% -Dapp.prop=%APPLICATION_PROPERTIES% -Dservices.prop=%SERVICES_PROPERTIES% -Dsigar.home=%SIGAR_HOME% -Dlog4j.configuration=file:%LOG_CONFIG%

call %SYSTEM_AGENT_HOME%\bin\classpath.bat

echo Launching java execution...
%JAVA_CMD% %SYSTEM_AGENT_JVM_ARGS% %IKR_JAVA_ARGS% com.fsi.monitoring.system.StartSystemAgent

