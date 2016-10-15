#!/bin/bash

IKR_APPLICATION_ENV="hortus"

JAVA_HOME="/usr/jdk1.5.0_17"

# SGBD - Select the type of database use for Indiktor
# [mysql/oracle]
DATABASE_TYPE="oracle"

COMPUTE_SERVER_RMI_REGISTRY_PORT=1099

COMPUTE_SERVER_JVM_ARGS="-Xmx512m -Xms256m"
MONITOR_SERVER_JVM_ARGS="-Xmx512m -Xms256m"
SCHEDULER_SERVER_JVM_ARGS="-Xmx512m -Xms256m"

