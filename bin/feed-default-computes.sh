#!/bin/bash

if [ -z "${IKR_HOME}" ]
then
	echo "The environment variable IKR_HOME does not exist, please add it before going further. It should be the directory where IndiKtor is installed"
	exit 1
fi

if [ $# -eq 0 ]
then
	echo "Usage: feed-default-computes RESET ikrLogicalEnv"
	exit 1
fi

source ${IKR_HOME}/bin/ikr-env.sh

APP_NAME="toolkits"

IKR_JAVA_ARGS="-Dikr.home=${IKR_HOME} -Dfile.propEnv=${APPLICATION_ENV_PROPERTIES} -Dlog4j.configuration=file:${LOG_CONF} -Dtoolkit.conf=${TOOLKIT_CONFIG_HOME} -Dtoolkit.resources=${TOOLKIT_RESOURCE_HOME} -Dikr.jdbc.databaseType=${DATABASE_TYPE}" 

source ${CLASSPATH_HOME}/${APP_NAME}-classpath.sh

${JAVA_CMD} ${IKR_JAVA_ARGS} -cp ${CLASSPATH} com.fsi.toolkits.crossCompute.DefaultCrossComputeFeeder $1 $2
