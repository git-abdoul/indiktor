#!/bin/bash

#--------------------------------------------------
# Description:
# Stop script of indiktor
# 
#
#
#
#--------------------------------------------------


usage () {
echo "usage : $0 Service_Name"
echo "Service Names:"
echo "[a or all or ALL or All] [logical_environment_name]: start all services for logical environment"
echo "[sys or sys_agent or system_agent]: start the system agent"
echo "[t or tom or tomcat]: start Tomcat"
echo "[activemq]: starts activemq"
echo "[comp or compute or computeserver]: start compute server"
echo "[col or collect or collector] [logical_environment_name]: start monitor server"


}

KillService () {
	_Service=$1
	if [ -f ${PID_DIRECTORY}/${HOSTNAME}.${_Service}.pid ]
	 then
	 _PID=`cat ${PID_DIRECTORY}/${HOSTNAME}.${_Service}.pid`
	 echo "Found process ${_Service} with PID ${_PID}"
	 echo "Killing process..."
	 kill ${_PID}
	 sleep 2
	 if [ `ps -p${_PID} | wc -l` -gt 1 ]; then
	 	kill -9 ${_PID}
	 fi
	 rm -f ${PID_DIRECTORY}/${HOSTNAME}.${_Service}.pid
#	 echo " just before the service:${_Service}"
	 ./scripts/indiktor_lock.sh ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file} remove ${_Service}
#	 echo " just after the service:${_Service}"
	fi
	unset _Service
	unset _PID
}

KillAll () {
	if [ ! -f `echo ${PID_DIRECTORY}/${HOSTNAME}.*.pid | cut -f1 -d' '`  ]
    then
    echo "No process running on this hostname."
    exit 0
   fi
   #Kills all services.
	for processPidFile in ${PID_DIRECTORY}/${HOSTNAME}.*.pid
	 do
	 _PID=`cat ${processPidFile}`
	 #_Service=`basename ${processPidFile} | cut -f2 -d'.'`
	 _Service=`basename ${processPidFile} | awk 'BEGIN {FS="."} { print $(NF-1)}'`
	 if [ `ps -p${_PID} | wc -l` -lt 2 ]
	  then
	   echo "Process ${_Service} seems to be dead."
	   echo "Removing pid file (`basename ${processPidFile}`)"
	   rm -f ${processPidFile}
	  else
	  	 echo "Found process ${_Service} with PID ${_PID}"
		 echo "Killing process..."
	    kill  ${_PID}
		 sleep 2
		 if [ `ps -p${_PID} | wc -l` -gt 1 ]; then
		  kill -9 ${_PID}
		 fi
		 rm -f ${PID_DIRECTORY}/${HOSTNAME}.${_Service}.pid
	 fi
	 ./scripts/indiktor_lock.sh ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file} remove ${_Service}
	 unset _PID
	done
}

#----------------------------------
#
# MAIN
#
#----------------------------------

# Default setting file
. ./scripts/indiktor_settings.sh

# Checking parameters.
if [ $# -eq 0 ]; then
 usage 
 exit 0
fi

LENV=`echo $1 | awk -F: '{print $2}'`

case $1 in


	h | help)
		    usage
		    exit 0
	;;
	
	activemq)
		#Killing services
		echo "Stopping ActiveMQ..."
		${ACTIVEMQ_HOME}/bin/activemq stop
		KillService ActiveMq
	;;

	comp | compute | computeserver )
		#Killing services
		echo "Stopping Compute Server..."
		KillService CompRMIRegistry
		KillService ComputeServer 
	;;

	col | collect | collector)
		#Killing services
		echo "Stopping Monitor Server..."
		KillService MonitorServer_${LOG_ENV}
	;;

	sys | sys_agent | system_agent )
		#Killing services
		echo "Stopping System-Agent..."
		KillService SysAgent
		KillService SysAgent_RMI
		KillService SysAgent_Jstad
	;;

	t | tom | tomcat )
		#Killing services
	    echo "Shutting down TOMCAT..."
		KillService Tomcat
	;;

	a | all | ALL | All )
		KillAll
	;;

	*)
		usage
	;;

esac

