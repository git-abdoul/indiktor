#!/bin/ksh

JSTATD_RMI_REGISTRY_PORT=9210

for PID1 in `/usr/ucb/ps axww | grep java | grep com.fsi.monitoring.system.StartSystemAgent | grep -v grep | awk '{print $1}'`
do
	echo "[`date`] System Agent is running with PID $PID1"
	kill $PID1  || error "[`date`] Cannot Kill System Agent Process"  
	echo "[`date`] System Agent $PID1 Killed"
done

for PID1 in `/usr/ucb/ps axww | grep rmiregistry | grep ${JSTATD_RMI_REGISTRY_PORT} | grep -v grep | awk '{print $1}'`
do
	echo "[`date`] RMI Registry is running with PID $PID1"
	kill $PID1  || error "[`date`] Cannot Kill RMI Registry Process"  
	echo "[`date`] RMI Registry $PID1 Killed"  
done

for PID1 in `/usr/ucb/ps axww | grep jstatd | grep ${JSTATD_RMI_REGISTRY_PORT} | grep -v grep | awk '{print $1}'`
do
	echo "[`date`] Jstatd is running with PID $PID1"
	kill $PID1  || error "[`date`] Cannot Kill Jstatd Process"  
	echo "[`date`] Jstatd $PID1 Killed"
done
