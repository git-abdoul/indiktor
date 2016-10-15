#!/bin/bash

cd ${IKR_HOME}

# WebServer
echo ----------------------------------------------------------
echo Stopping Web Server
echo ----------------------------------------------------------
./stop-webserver.sh

echo
echo
echo
# Compute Server
echo ----------------------------------------------------------
echo Stopping Compute Server
echo ----------------------------------------------------------
./bin/stop-compute-server.sh

echo
echo
echo
echo ----------------------------------------------------------
echo Stopping Monitor Servers
echo ----------------------------------------------------------

# DEMO Monitor Server
./bin/stop-monitor-server.sh DEMO

echo
echo
echo
echo ----------------------------------------------------------
echo Stopping Scheduler Server
echo ----------------------------------------------------------

# DEMO Scheduler Server
./bin/stop-scheduler-server.sh DEMO