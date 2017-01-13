@echo off

if "%SYSTEM_AGENT_HOME%" == "" 
echo The environment variable SYSTEM_AGENT_HOME does not exist, please add it before going further. It should be the directory where System Agent is installed
EXIT /B 1

call %SYSTEM_AGENT_HOME%\settings.bat

start %JAVA_HOME%\bin\rmiregistry %JSTATD_RMI_PORT%
%JAVA_HOME%\bin\jstatd -J-Djava.security.policy=jstatd.all.policy -p %JSTATD_RMI_PORT% -n IndiktorSystemAgentHostRemote