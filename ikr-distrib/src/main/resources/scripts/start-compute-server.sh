#!/bin/bash

if [ -z "${IKR_HOME}" ]
then
	echo "The environment variable IKR_HOME does not exist, please add it before going further. It should be the directory where IndiKtor is installed"
	exit 1
fi

source ${IKR_HOME}/bin/ikr-env.sh

APP_NAME="compute-server"
JAR="${IKR_LIBS_HOME}/compute-server-${IKR_VERSION}.jar"
FOUNDATION="${IKR_LIBS_HOME}/foundation-${IKR_VERSION}.jar"

IKR_JAVA_ARGS="-Dapp.id=Indiktor_ComputeServer -Dfile.propEnv=${APPLICATION_ENV_PROPERTIES} -Dsnmp.mibFile=${SNMP_MIB_FILE} -Dikr.jdbc.databaseType=${DATABASE_TYPE}"

source ${CLASSPATH_HOME}/${APP_NAME}-classpath.sh

rmiregistry ${COMPUTE_SERVER_RMI_REGISTRY_PORT} &
echo "RMI Registry Started"

${JAVA_CMD} ${COMPUTE_SERVER_JVM_ARGS} ${IKR_JAVA_ARGS} -Djava.rmi.server.codebase="file:${JAR} file:${FOUNDATION}" -cp ${CLASSPATH} com.fsi.monitoring.computeServer.apps.StartComputeServer &
