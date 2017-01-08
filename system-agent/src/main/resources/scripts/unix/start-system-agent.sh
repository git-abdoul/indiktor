#!/bin/bash

DISTRIB=/proj/QA/system-agent
JAVA_HOME="/usr/java/jdk1.6.0_26"
SYSTEM_AGENT_HOME=$DISTRIB
SYSTEM_AGENT_PIDS=$SYSTEM_AGENT_HOME/bin/PID
SIGAR_HOME=$SYSTEM_AGENT_HOME/lib/sigar-1.6.3.0
JSTATD_RMI_PORT=3535
JAVA_CMD="${JAVA_HOME}/jre/bin/java"
HOSTNAME=`hostname`
JVM_ARGS="-Xmx128m -Xms64m"

. $SYSTEM_AGENT_HOME/bin/classpath.sh

echo Launching java execution...

echo Starting JStatd RMI Registry on port $JSTATD_RMI_PORT
rmiregistry $JSTATD_RMI_PORT &
echo "$!" > $SYSTEM_AGENT_PIDS/${HOSTNAME}.SysAgent_RMI.pid

echo Starting JStatd Daemon
jstatd -J-Djava.security.policy=jstatd.all.policy -p $JSTATD_RMI_PORT -n IndiktorSystemAgentHostRemote &
echo "$!" > $SYSTEM_AGENT_PIDS/${HOSTNAME}.SysAgent_Jstad.pid

echo Starting System Agent
"$JAVA_CMD" $JVM_ARGS -cp $CLASSPATH -Djava.rmi.server.hostname=$HOSTNAME -DhomeDir=$SYSTEM_AGENT_HOME -Dsigar.home=$SIGAR_HOME -Dlog4j.configuration=file:$SYSTEM_AGENT_HOME/conf/log4j.xml com.fsi.monitoring.system.StartSystemAgent &
echo "$!" > $SYSTEM_AGENT_PIDS/${HOSTNAME}.SysAgent.pid


