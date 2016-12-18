#!/bin/bash

cd ${IKR_HOME}

# Compute Server
echo ----------------------------------------------------------
echo Starting Compute Server
echo ----------------------------------------------------------
./bin/start-compute-server.sh &

echo
echo
echo
echo waiting ...
sleep 15

echo
echo
echo
# WebServer
echo ----------------------------------------------------------
echo Starting Web Server
echo ----------------------------------------------------------
./start-webserver.sh &

echo
echo
echo
echo waiting ...
sleep 15
# DEMO Monitor Server
echo ----------------------------------------------------------
echo Starting Monitor Server DEMO
echo ----------------------------------------------------------
./bin/start-monitor-server.sh DEMO > DEMO_MONITOR_NOHUP &

echo
echo
echo
# DEMO Scheduler Server
echo ----------------------------------------------------------
echo Starting Scheduler Server DEMO
echo ----------------------------------------------------------
./bin/start-scheduler-server.sh DEMO > DEMO_SCHEDULER_NOHUP &