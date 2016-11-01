#!/bin/bash
# Usage: stop-monitor-server ikrLogicalEnv

if [ -z "${IKR_HOME}" ]
then
	echo "The environment variable IKR_HOME does not exist, please add it before going further. It should be the directory where IndiKtor is installed"
	exit 1
fi

if [ $# -eq 0 ]
then
	echo "Usage: start-monitor-server ikrLogicalEnv"
	exit 1
fi

source ${IKR_HOME}/bin/ikr-env.sh

APP_NAME="monitor-server"

IKR_JAVA_ARGS="-Dapp.id=Indiktor_MonitorServer -Dfile.propEnv=${APPLICATION_ENV_PROPERTIES} -Dikr.jdbc.databaseType=${DATABASE_TYPE} -Dikr.resource.dir=${COLLECTOR_RESOURCE_HOME}"

CLASSPATH_CUSTOM_FILE="${CLASSPATH_HOME}/${APP_NAME}-classpath$1.sh"
if [ -s ${CLASSPATH_CUSTOM_FILE} ]
then
  source ${CLASSPATH_CUSTOM_FILE}
fi
source ${CLASSPATH_HOME}/${APP_NAME}-classpath.sh 

${JAVA_CMD} ${MONITOR_SERVER_JVM_ARGS} ${IKR_JAVA_ARGS} -Dapp.env=$1 -cp ${CLASSPATH} com.fsi.monitoring.server.apps.StartMonitorServer $1 &
