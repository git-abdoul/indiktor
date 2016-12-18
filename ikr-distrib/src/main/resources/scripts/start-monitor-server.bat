@echo off

REM if "%IKR_HOME%" == "" 
REM echo The environment variable IKR_HOME does not exist, please add it before going further. It should be the directory where IndiKtor is installed
REM EXIT /B 1

call %IKR_HOME%\bin\ikr-env.bat

SET APP_NAME=monitor-server

SET IKR_JAVA_ARGS=-Dfile.propEnv=%APPLICATION_ENV_PROPERTIES% -Dikr.jdbc.databaseType=%DATABASE_TYPE% -Dikr.resource.dir=%COLLECTOR_RESOURCE_HOME%

SET CLASSPATH_CUSTOM_FILE=%CLASSPATH_HOME%\%APP_NAME%-classpath%1%.bat
if EXIST %CLASSPATH_CUSTOM_FILE% (
	call %CLASSPATH_CUSTOM_FILE%
)
call %CLASSPATH_HOME%\%APP_NAME%-classpath.bat

%JAVA_CMD% %MONITOR_SERVER_JVM_ARGS% %IKR_JAVA_ARGS% com.fsi.monitoring.server.apps.StartMonitorServer %1