@echo off

REM if "%IKR_HOME%" == "" 
REM echo The environment variable IKR_HOME does not exist, please add it before going further. It should be the directory where IndiKtor is installed
REM EXIT /B 1

call %IKR_HOME%\bin\ikr-env.bat

SET APP_NAME=compute-server
SET JAR=%IKR_LIBS_HOME%\compute-server-%IKR_VERSION%.jar
SET FOUNDATION=%IKR_LIBS_HOME%\foundation-%IKR_VERSION%.jar

SET IKR_JAVA_ARGS=-Dfile.propEnv=%APPLICATION_ENV_PROPERTIES% -Dsnmp.mibFile=%SNMP_MIB_FILE% -Djava.rmi.server.codebase="file:%JAR% file:%FOUNDATION%" -Dikr.jdbc.databaseType=%DATABASE_TYPE%

call %CLASSPATH_HOME%\%APP_NAME%-classpath.bat

%JAVA_CMD% %COMPUTE_SERVER_JVM_ARGS% %IKR_JAVA_ARGS% com.fsi.monitoring.computeServer.apps.StartComputeServer