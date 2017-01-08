@echo off

REM if "%IKR_HOME%" == "" 
REM echo The environment variable IKR_HOME does not exist, please add it before going further. It should be the directory where IndiKtor is installed
REM EXIT /B 1

call %IKR_HOME%\bin\ikr-env.bat

SET APP_NAME=toolkits

SET IKR_JAVA_ARGS=-Dikr.home=%IKR_HOME% -Dfile.propEnv=%APPLICATION_ENV_PROPERTIES% -Dikr.jdbc.databaseType=%DATABASE_TYPE% -Dtoolkit.conf=%TOOLKIT_CONFIG_HOME% -Dtoolkit.resources=%TOOLKIT_RESOURCE_HOME%

call %CLASSPATH_HOME%\%APP_NAME%-classpath.bat

%JAVA_CMD% %IKR_JAVA_ARGS% com.fsi.toolkits.crossCompute.DefaultCrossComputeFeeder %1 %2