#!/bin/ksh

if [ -z "${SYSTEM_AGENT_HOME}" ]
then
	echo "The environment variable SYSTEM_AGENT_HOME does not exist, please add it before going further. It should be the directory where System Agent is installed"
	exit 1
fi

source ${SYSTEM_AGENT_HOME}/settings.sh

for PID1 in `/usr/ucb/ps axww | grep java | grep Indiktor_SystemAgent | grep -v grep | awk '{print $1}'`
do
	echo "[`date`] System Agent is running with PID $PID1"
	kill $PID1  || error "[`date`] Cannot Kill System Agent Process"  
	echo "[`date`] System Agent $PID1 Killed"
done

for PID2 in `/usr/ucb/ps axww | grep rmiregistry | grep ${JSTATD_RMI_REGISTRY_PORT} | grep -v grep | awk '{print $1}'`
do
	echo "[`date`] RMI Registry is running with PID $PID2"
	kill $PID2  || error "[`date`] Cannot Kill RMI Registry Process"  
	echo "[`date`] RMI Registry $PID2 Killed"  
done

for PID3 in `/usr/ucb/ps axww | grep jstatd | grep ${JSTATD_RMI_REGISTRY_PORT} | grep -v grep | awk '{print $1}'`
do
	echo "[`date`] Jstatd is running with PID $PID3"
	kill $PID3  || error "[`date`] Cannot Kill Jstatd Process"  
	echo "[`date`] Jstatd $PID3 Killed"
done
