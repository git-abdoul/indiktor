#!/bin/bash
# Usage: stop-scheduler-server environment

if [ -z "${IKR_HOME}" ]
then
	echo "The environment variable IKR_HOME does not exist, please add it before going further. It should be the directory where IndiKtor is installed"
	exit 1
fi

if [ $# -eq 0 ]
then
	echo "Usage: start-scheduler-server ikrLogicalEnv"
	exit 1
fi

source ${IKR_HOME}/bin/ikr-env.sh

APP_NAME="scheduler-server"

IKR_JAVA_ARGS="-Dapp.id=Indiktor_SchedulerServer -Dfile.propEnv=${APPLICATION_ENV_PROPERTIES} -Dikr.jdbc.databaseType=${DATABASE_TYPE}" 

CLASSPATH_CUSTOM_FILE="${CLASSPATH_HOME}/${APP_NAME}-classpath$1.sh"
if [ -s ${CLASSPATH_CUSTOM_FILE} ]
then
  source ${CLASSPATH_CUSTOM_FILE}
fi
source ${CLASSPATH_HOME}/${APP_NAME}-classpath.sh 

${JAVA_CMD} ${SCHEDULER_SERVER_JVM_ARGS} ${IKR_JAVA_ARGS} -cp ${CLASSPATH} -Dapp.env=$1 com.fsi.scheduler.apps.StartSchedulerServer $1 &
