#!/bin/bash

AMQ_PID=`ps -ef | grep java | grep run.jar | grep activemq | awk '{print $2}'`	
if [ ${#AMQ_PID} -le 0 ]; 
then
	echo Active MQ  --  [NOK]
else
	echo Active MQ  -- [OK - $AMQ_PID]
fi
	
WEB_PID=`ps -ef | grep java | grep org.apache.catalina.startup.Bootstrap | awk '{print $2}'`	
if [ ${#WEB_PID} -le 0 ]; 
then
	echo Indiktor WebServer  --  [NOK]
else
	echo Indiktor WebServer  -- [OK - $WEB_PID]
fi
	
RMI_REGISTRY_PORT=1099
RMIREGISTRY_PID=`ps -ef | grep rmiregistry | grep $RMI_REGISTRY_PORT | awk '{print $2}'`	
if [ ${#RMIREGISTRY_PID} -le 0 ]; 
then
	echo RMI Registry  --  [NOK]
else
	echo RMI Registry  -- [OK - $RMIREGISTRY_PID]
fi

CS_PID=`ps -ef | grep java | grep Indiktor_ComputeServer | awk '{print $2}'`
if [ ${#CS_PID} -le 0 ]; 
then
	echo Compute Server  --  [NOK]
else
	echo Compute Server  -- [OK - $CS_PID]
fi

MONITOR_PID=`ps -ef | grep java | grep Indiktor_MonitorServer | grep DEMO | awk '{print $2}'`	
if [ ${#MONITOR_PID} -le 0 ]; 
then
	echo Monitor Server DEMO  --  [NOK]
else
	echo Monitor Server DEMO  -- [OK - $MONITOR_PID]
fi

SCHEDULER_PID=`ps -ef | grep java | grep Indiktor_SchedulerServer | grep DEMO | awk '{print $2}'`	
if [ ${#SCHEDULER_PID} -le 0 ]; 
then
	echo Scheduler Server DEMO  --  [NOK]
else
	echo Scheduler Server DEMO  -- [OK - $SCHEDULER_PID]
fi