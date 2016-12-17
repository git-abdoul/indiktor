@echo off

SET IKR_APPLICATION_ENV=petrus

REM SGBD - Select the type of database use for Indiktor
REM [mysql/oracle]
SET DATABASE_TYPE=mysql

SET JAVA_HOME=C:\tools\java\jdk1.6.0_37

SET COMPUTE_SERVER_JVM_ARGS=-Xmx512m -Xms256m
SET MONITOR_SERVER_JVM_ARGS=-Xmx1024m -Xms512m
SET SCHEDULER_SERVER_JVM_ARGS=-Xmx512m -Xms256m