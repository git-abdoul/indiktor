#!/bin/bash
# Usage: stop-scheduler-server ikrLogicalEnv

if [ $# -eq 0 ]
then
	echo "Usage: stop-scheduler-server ikrLogicalEnv"
	exit 1
fi

IKR_LOGICAL_ENV=$1
SCHEDULER_PID=`ps -ef | grep java | grep Indiktor_SchedulerServer | grep $IKR_LOGICAL_ENV | awk '{print $2}'`

error() 
{
	echo ERROR : $1
}

if [ ${#SCHEDULER_PID} -le 0 ]; 
then
	error "No Scheduler Server $IKR_LOGICAL_ENV started"
else
	echo Scheduler Server $IKR_LOGICAL_ENV is running with pid $SCHEDULER_PID
	kill $SCHEDULER_PID || error "Cannot kill process Scheduler Server $IKR_LOGICAL_ENV"
	echo Scheduler Server $IKR_LOGICAL_ENV  $SCHEDULER_PID killed
fi

