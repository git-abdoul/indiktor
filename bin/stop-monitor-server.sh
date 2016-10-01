#!/bin/bash
# Usage: stop-monitor-server ikrLogicalEnv

if [ $# -eq 0 ]
then
	echo "Usage: stop-monitor-server ikrLogicalEnv"
	exit 1
fi

IKR_LOGICAL_ENV=$1
MONITOR_PID=`ps -ef | grep java | grep Indiktor_MonitorServer | grep $IKR_LOGICAL_ENV | awk '{print $2}'`

error() 
{
	echo ERROR : $1
}

if [ ${#MONITOR_PID} -le 0 ]; 
then
	error "No Monitor Server $IKR_LOGICAL_ENV started"
else
	echo Monitor Server $IKR_LOGICAL_ENV is running with pid $MONITOR_PID
	kill $MONITOR_PID || error "Cannot kill process Monitor Server $IKR_LOGICAL_ENV"
	echo Monitor Server $IKR_LOGICAL_ENV  $MONITOR_PID killed
fi



