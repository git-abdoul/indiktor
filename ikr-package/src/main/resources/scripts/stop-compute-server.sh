#!/bin/bash

if [ -z "${IKR_HOME}" ]
then
	echo "The environment variable IKR_HOME does not exist, please add it before going further. It should be the directory where IndiKtor is installed"
	exit 1
fi

source ${IKR_HOME}/bin/ikr-env.sh

CS_PID=`ps -ef | grep java | grep Indiktor_ComputeServer | awk '{print $2}'`	

RMIREGISTRY_PID=`ps -ef | grep rmiregistry | grep ${COMPUTE_SERVER_RMI_REGISTRY_PORT} | awk '{print $2}'`	

error() 
{
	echo ERROR : $1
}

if [ ${#RMIREGISTRY_PID} -le 0 ]; 
then
	error "No RMI Registry started"
else
	echo RMI Registry is running with PID $RMIREGISTRY_PID
	kill $RMIREGISTRY_PID  || error "Cannot kill RMI Registry Process"  
	echo RMI Registry $RMIREGISTRY_PID killed 
fi

if [ ${#CS_PID} -le 0 ]; 
then
	error "No Compute Server started"
else
	echo Compute Server is running with PID $CS_PID
	kill $CS_PID  || error "Cannot kill Compute Server Process"  
	echo Compute Server $CS_PID killed 
fi
