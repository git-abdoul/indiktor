
#!/bin/bash

#--------------------------------------------------
# Description:
# Start script of indiktor
# 
#
#
#
#--------------------------------------------------


# Default setting file
. ./scripts/indiktor_settings.sh


usage () {
echo "usage : $0 [option]"
echo "Options:"
echo "[s or status]: current status"
echo "[a or all or ALL or All] [logical_environment_name]: start all services for logical environment"
echo "[sys or sys_agent or system_agent]: start the system agent"
echo "[t or tom or tomcat]: start Tomcat"
echo "[activemq]: starts activemq"
echo "[comp or compute or computeserver]: start compute server"
echo "[col or collect or collector] [logical_environment_name]: start monitor server"
}

GetOptions () {

#CleaningOptions

	computeserver=0
	monitorserver=0
	activemq=0
	sysagent=0
	tomcat=0
	allservices=0

	while [ $# -ne 0 ]
	do
	ARG=$1
	VAL=$2
	case ${ARG} in
		h | help)
		    usage
		    exit 0
		;;
		comp | compute | computeserver )
		    computeserver=1
		;;
		col | collect | collector)
		    monitorserver=1
		    LENV=${VAL} 
		;;
		activemq )
		    activemq=1
		;;
		status | s )
		    CheckServices
		;;
		sys | sys_agent | system_agent )
		    sysagent=1
		;;
		t | tom | tomcat )
		    tomcat=1
		;;
		a | all | ALL | All )
		 	allservices=1
		 	tomcat=1
		 	sysagent=1
		    activemq=1
		    monitorserver=1
		    computeserver=1
		    LENV=${VAL} 
		;;
		*)
			usage
		;;
	esac
	shift
	done
}

CheckTomcatIndiktorConfiguration()
{
	_ind_dir=$1
	_tomcat_ind_dir=${_ind_dir}/apps/tomcat/webapps/indiktor
	_par_to_insert=""

	if [ -d $_tomcat_ind_dir ]; then

		_file=${_tomcat_ind_dir}/WEB-INF/applicationContext.xml

		_line_number=`cat $_file | grep -in persistency | awk -F: '{print $1}'`
		_par_to_insert='<import resource="file:'${_ind_dir}'/indiktor-suite/component/persistency/conf/local/persistencyContext.xml"/>'

		if [ ! -z "$_par_to_insert" ]; then 
			line_insert="$_line_number c $_par_to_insert"
			sed "$line_insert" $_file > ./tmp_file
			mv ./tmp_file $_file
		fi

		_line_number=`cat $_file | grep -in boardroot | awk -F: '{print $1}'`
		_par_to_insert='<property name="boardRoot" 	value="'${_ind_dir}'/indiktor-suite/conf/dashboard"/>'

		if [ ! -z "$_par_to_insert" ]; then 
			line_insert="$_line_number c $_par_to_insert"
			sed "$line_insert" $_file > ./tmp_file
			mv ./tmp_file $_file
		fi

		_file=${_tomcat_ind_dir}/WEB-INF/classes/log4j.xml
		_line_number=`cat $_file | grep -in indiktor-server | awk -F: '{print $1}'`
		_par_to_insert='<param name="file" value="'${_ind_dir}'/indiktor-suite/logs/indiktor-server.log" />'

		if [ ! -z "$_par_to_insert" ]; then 
			line_insert="$_line_number c $_par_to_insert"
			sed "$line_insert" $_file > ./tmp_file
			mv ./tmp_file $_file
		fi

	fi
}

CheckProcessExistenz () {
	_Service=${1}
	case $_Service in
		SysAgent)
			_pid=`ps -ef | grep java | grep com.fsi.monitoring.system.StartSystemAgent | grep -v grep | awk '{print $2}'`
			if [ ! -z $_pid ]; then
				echo "Service ${_Service} has already started."
				#verification that the system-agent are already in input ow. add them to the lock indiktor file
				if [ -f ${INDIKTOR_PROGRESS_DIR}/${_in_progress_file} ]; then
					_instance=`sed -n '1p' ${INDIKTOR_PROGRESS_DIR}/${_in_progress_file} | awk -F: '{print $2}'`
					_instance=$_instance"/scripts"
#					echo "_instance=$_instance"
					_curr_instance=`pwd`
#					echo "_curr_instance=$_curr_instance"
					if [ $_instance = $_curr_instance ]; then				
						_pid_list=$SYSTEM_AGENT_HOME/bin/logs/*.pid
				        	for _pid_file in $_pid_list
				        	do
				        		_tmp_pid=`cat "$_pid_file"`
				        		_service=`basename $_pid_file | awk 'BEGIN {FS="."} { print $(NF-1)}'`
				        		_occ_service=`grep -ic $_service ${INDIKTOR_PROGRESS_DIR}/${_in_progress_file}`
				        		if [ $_occ_service -eq 0 ]; then
				        			./scripts/indiktor_lock.sh ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file} service $_service $_tmp_pid
				        		fi
				        	done
					fi
				fi
				return 1  
			else
				
				return 0
			fi
		;;

		*)
			if [ -f ${PID_DIRECTORY}/${HOSTNAME}.${_Service}.pid ]
			then
				echo "Service ${_Service} might already been started. Please check below."
				CheckServices
				return 1  
			else
				return 0
			fi
		;;
	esac
	unset _Services
}

CheckServices () {
	if [ ! -f `echo ${PID_DIRECTORY}/${HOSTNAME}.*.pid | cut -f1 -d' '`  ]
	then
		echo "No process running on this hostname."
		exit 0
	fi
	echo "List of processes running on ${HOSTNAME}."
	for processPidFile in ${PID_DIRECTORY}/${HOSTNAME}.*.pid
	do
	_PID=`cat ${processPidFile}`
	if [ `ps -p${_PID} | wc -l` -lt 2 ]
	then
	   	echo "Process `basename ${processPidFile} | cut -f2 -d'.'` seems to be dead."
	   	echo "Removing pid file (`basename ${processPidFile}`)"
	   	rm -f ${processPidFile}
	    _Service=`basename ${processPidFile} | awk 'BEGIN {FS="."} { print $(NF-1)}'`
	    ./scripts/indiktor_lock.sh ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file} remove ${_Service}
	else
		echo "`basename ${processPidFile}` - PID ${_PID}"
	fi
		unset _PID
	done
}

fn_activemq(){

	if [ ${activemq} -eq 1 ]
	 then
		if CheckProcessExistenz ActiveMq
   		then
		  echo "Starting ActiveMQ..."
		  ${ACTIVEMQ_HOME}/bin/activemq start
	      cp ${ACTIVEMQ_HOME}/data/activemq.pid ${PID_DIRECTORY}/${HOSTNAME}.ActiveMq.pid
	      _tmp_pid=`cat ${PID_DIRECTORY}/${HOSTNAME}.ActiveMq.pid`
	      ./scripts/indiktor_lock.sh ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file} service ActiveMq $_tmp_pid
		fi 
	fi
}

fn_compute_server(){

	if [ ${computeserver} -eq 1 ]
	then 
		if CheckProcessExistenz CompRMIRegistry
		then
		   	rmiregistry ${COMPUTE_PORT} &
		   	_tmp_pid="$!"
		   	./scripts/indiktor_lock.sh ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file} service CompRMIRegistry $_tmp_pid
		   	echo "$_tmp_pid" >${PID_DIRECTORY}/${HOSTNAME}.CompRMIRegistry.pid
			sleep 5
		   	echo "RMI Registry Started"
		else
		   	echo "Service already started."
		fi
		if CheckProcessExistenz ComputeServer
		then
		  	echo "Starting compute server"

		  	echo "${COMP_JVM_ARGS} -Dapp.id=Indiktor_ComputeServer -cp ${COMP_CLASSPATH} -Djava.rmi.server.codebase='file:${COMP_JAR} file:${COMP_FOUNDATION} -DapplicationContextPath=file:${COMP_APPLICATION_CONTEXT} -Dlog4j.configuration=file:${COMP_LOG_CONFIGURATION} com.fsi.monitoring.computeServer.apps.StartComputeServer"
			"$JAVA_CMD" ${COMP_JVM_ARGS} -Dapp.id=Indiktor_ComputeServer -cp ${COMP_CLASSPATH} -Djava.rmi.server.codebase="file:${COMP_JAR} file:${COMP_FOUNDATION}" -DapplicationContextPath=file:${COMP_APPLICATION_CONTEXT} -Dlog4j.configuration=file:${COMP_LOG_CONFIGURATION} com.fsi.monitoring.computeServer.apps.StartComputeServer &
			_tmp_pid="$!"
			./scripts/indiktor_lock.sh ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file} service ComputeServer $_tmp_pid
			echo "$_tmp_pid" > ${PID_DIRECTORY}/${HOSTNAME}.ComputeServer.pid
			sleep 10
		else
		   	echo "Service already started."
		fi
	fi
}

fn_monitor_server(){
	if [ ${monitorserver} -eq 1 ]
	then
	   	fn_logenv_check
		   	if [ $_result_log -ne 0 ]; then 
				if CheckProcessExistenz MonitorServer_${LENV}
				then
				    if [ -f ${MONIT_CLASSPATH_CLIENT}/classpath${LENV}.sh ]
				    then
				       echo "Using specific classpath script (${MONIT_CLASSPATH_CLIENT}/classpath${LENV}.sh)"
				       source ${MONIT_CLASSPATH_CLIENT}/classpath${LENV}.sh
				       MONIT_CLASSPATH=${CLASSPATH}:${MONIT_CLASSPATH}
				    fi
				    
				    "$JAVA_CMD" ${MONIT_JVM_ARGS} -Ddistrib=${INDIKTOR_SUITE_HOME} -Dapp.id=Indiktor_MonitorServer -Dapp.env=${LENV} -cp ${MONIT_CLASSPATH} -DapplicationContextPath=file:${MONIT_APPLICATION_CONTEXT} -Dlog4j.configuration=file:${MONIT_LOG_CONFIGURATION} com.fsi.monitoring.server.apps.StartMonitorServer ${LENV} &
				    _tmp_pid="$!"
				  	./scripts/indiktor_lock.sh ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file} service MonitorServer_${LENV} $_tmp_pid
				   	echo "$_tmp_pid" > ${PID_DIRECTORY}/${HOSTNAME}.MonitorServer_${LENV}.pid
					sleep 5
				else
				    echo "Service already started."
				fi
			fi
	fi
}

fn_system_agent_server(){

	fn_os_lib_version
	if [ `echo $sys_agent_lib | wc -m` -gt 1 ]; then
		if [ ${sysagent} -eq 1 ]
		then
		    if CheckProcessExistenz SysAgent
		    then
		        echo "Starting System-Agent..."
		        $SYSTEM_AGENT_HOME/bin/start-system-agent.sh
		        sleep 5
		        cp -rf $SYSTEM_AGENT_HOME/bin/logs/*.pid ${PID_DIRECTORY}/
		        _pid_list=$SYSTEM_AGENT_HOME/bin/logs/*.pid
	        	for _pid_file in $_pid_list
	        	do
	        		_tmp_pid=`cat "$_pid_file"`
	        		_service=`basename $_pid_file | awk 'BEGIN {FS="."} { print $(NF-1)}'`
	        		./scripts/indiktor_lock.sh ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file} service $_service $_tmp_pid
	        	done
		    else
		     	echo "Service already started."
		   	fi
		fi
	else
		echo "SYS_AGENT_LIB=$sys_agent_lib"
		echo "WARNING: the dynamic library of the system agent could not be attributed"
		echo "System Agent Failed to Start"
		echo "Note that only the libraries for the versions are available: "
		echo "Linux 32 bits"
		echo "Solaris 32 bits"
		echo "Solaris SPARC 32 bits"
		echo "Solaris SPARC 64 bits"
		echo "Solaris Intel 32 bits"
		echo "Solaris AMD 64 bits"
	fi
}

fn_tomcat(){
	## Tomcat ##
	if [ ${tomcat} -eq 1 ]
	 then
	    if CheckProcessExistenz Tomcat
	    then
		    echo "Starting TOMCAT..."
			[ -f "$CATALINA_OUT" ] || ( touch "$CATALINA_OUT" )			  
			"$JAVA_CMD" "$LOGGING_CONFIG" $JAVA_OPTS \
			-Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH_TOMCAT" \
			-Dcatalina.base="$TOMCAT_ROOT_HOME" \
			-Dcatalina.home="$TOMCAT_ROOT_HOME" \
			-Djava.io.tmpdir="$CATALINA_TMPDIR" \
			org.apache.catalina.startup.Bootstrap start \
			>> "$CATALINA_OUT" 2>&1 &
		  	_tmp_pid="$!"
		  	./scripts/indiktor_lock.sh ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file} service Tomcat $_tmp_pid
			echo "$_tmp_pid">${PID_DIRECTORY}/${HOSTNAME}.Tomcat.pid
			sleep 5
			CheckTomcatIndiktorConfiguration ${INDIKTOR_ROOT_HOME}
	    else
	    	echo "Service already started."
	    fi
	fi
}

fn_os_lib_version(){
#default sigar library
sys_agent_lib=$SYSTEM_AGENT_LIB_LINUX

os_linux="linux"
os_sunos="sunos"

os_version=`uname -s | tr [A-Z] [a-z]`

case $os_version in
	$os_linux | $os_sunos)
	;;
	*)
	#default os: linux
	os_version=$os_linux
	;;
esac

os_sun_types="amd sparc i386"
os_sun_bits="32 64"

_type="amd"
_bits="32"

if [ $os_version = $os_linux ]; then
	 
	 _processor_type=`uname -p`
	 case ${_processor_type} in
		
		"x86_64" )
		sys_agent_lib=$SYSTEM_AGENT_LIB_LINUX_I64
		;;
		
		* )
		sys_agent_lib=$SYSTEM_AGENT_LIB_LINUX
		;;
	 esac
fi

if [ $os_version = $os_sunos ]; then
	_info_proc=`uname -p | tr [A-Z] [a-z]`
	_info_bits=`isainfo -b | tr [A-Z] [a-z]`

	for i in $os_sun_types

	do
		
		# echo "in first loop: $_info_proc"
		n=`echo "$_info_proc" | grep -c $i`

		if [ $n -ge 1 ]; then 
			_type=$i
			break
		fi
	done
	
	for i in $os_sun_bits
	do
		n=`echo "$_info_bits" | grep -c $i`
		if [ $n -ge 1 ]; then 
			_bits=$i
			break
		fi
	done

	version_solaris=$_type$_bits

	case "$version_solaris" in
		"sparc32")
		echo "INFO: Identified architecture as SPARC 32 bit: Using required library..."
		sys_agent_lib=$SYSTEM_AGENT_LIB_SOLARIS_SPARC32
		;;
		"sparc64")
		echo "INFO: Identified architecture as SPARC 64 bit: Using required library..."
		sys_agent_lib=$SYSTEM_AGENT_LIB_SOLARIS_SPARC64
		;;
		"i38632")
		echo "INFO: Identified architecture as x86 32 bit: Using required library..."
		sys_agent_lib=$SYSTEM_AGENT_LIB_SOLARIS_I32
		;;
		"i38664")
		echo "INFO: Identified architecture as x86 64 bit: Using required library..."
		sys_agent_lib=$SYSTEM_AGENT_LIB_SOLARIS_I64
		;;
		"amd64")
		echo "INFO: Identified architecture as AMD 64 bit: Using required library..."
		sys_agent_lib=$SYSTEM_AGENT_LIB_SOLARIS_AMD64
		;;
		*)
		echo "WARNING: Unable to identify the architecture... using default library for Solaris (SPARC 32 bit)"
		sys_agent_lib=$SYSTEM_AGENT_LIB_SOLARIS_SPARC32 #default library value taken if no case has been found for solaris
		;;
	esac
fi

local SYS_AGENT_FILE=${SYSTEM_AGENT_HOME}/conf/application.properties
if [ ! -f ${SYS_AGENT_FILE} ]; then
 			echo "ERROR: The file ${SYS_AGENT_FILE} does not exist!"
 			echo "       System Agent might not work properly..."
 		else

			linetochange=`cat "${SYS_AGENT_FILE}" | grep 'system_agent.sigar.lib.name'`
			# First find the char number from which to extract.
			CharFromWhichToExtract=`expr match "${linetochange}" '.*sigar.lib.name='`
			CUR_LIB_NAME=`echo "$linetochange" | cut -c${CharFromWhichToExtract}- | cut -f2 -d'='`
			cp ${SYS_AGENT_FILE} ${SYS_AGENT_FILE}.tmp
			sed "s|${CUR_LIB_NAME}|${sys_agent_lib}|g" ${SYS_AGENT_FILE} > ${SYS_AGENT_FILE}.tmp
			mv ${SYS_AGENT_FILE}.tmp ${SYS_AGENT_FILE}
fi

}

checkExists(){	
		_result=`grep -c "$1" "$2"`
}

fn_logenv_check(){

	_result_log=0

	# CONSTRUCT SQL Statement
	query='select NAME from LOGICAL_ENV where NAME like @env'

	# CREATE SQL Query into temp .sql script file
	echo "${query}" > temp.sql

	# EXECUTE Query
#	cat temp.sql
	mysql -e "set @env:='${LENV}';source temp.sql;" -u ${DB_USER} -p ${DB_NAME} -s -N --password=${DB_PASSWORD} > temp.out 2>/dev/null
	if [[ -s temp.out ]]; then
		echo "Logical Environment Check: SUCCESS"
		_result_log=1
	else
		echo "Logical Environment Check: ERROR: Logical Environment ${LENV} Not Created"
	fi

	rm temp.out
	rm temp.sql
}


# Loading global classpaths.
############################

#Compute Server Default configuration
DISTRIB=${INDIKTOR_SUITE_HOME}
COMP_APPLICATION_CONTEXT=${INDIKTOR_SUITE_HOME}/component/compute-server/conf/local/applicationContext.xml
COMP_JAR=${INDIKTOR_SUITE_HOME}/component/compute-server/distrib/compute-server-${INDIKTOR_VERSION}.jar
COMP_FOUNDATION=${INDIKTOR_SUITE_HOME}/component/foundation/distrib/foundation-${INDIKTOR_VERSION}.jar
COMP_LOG_CONFIGURATION=${INDIKTOR_SUITE_HOME}/component/compute-server/conf/local/log4j.xml
COMP_JVM_ARGS=${COMP_JVM_ARGS}" -Xms256m -Xmx512m"
#Compute Server default classpath
COMP_CLASSPATH_FILE=${INDIKTOR_SUITE_HOME}/component/compute-server/bin/classpath.sh
. $COMP_CLASSPATH_FILE
COMP_CLASSPATH=$CLASSPATH


# NOTE : case 213 is open asking for a review of the CLASSPATH.
# Compute Server Default configuration : END

#Monitor Server Default configuration
MONIT_APPLICATION_CONTEXT=${INDIKTOR_SUITE_HOME}/component/monitor-server/conf/local/applicationContext.xml
MONIT_LOG_CONFIGURATION=${INDIKTOR_SUITE_HOME}/component/monitor-server/conf/local/log4j.xml
MONIT_CLASSPATH_CLIENT=${INDIKTOR_SUITE_HOME}/component/monitor-server/bin/
MONIT_JVM_ARGS=${MONIT_JVM_ARGS}" -Xmx512m -Xms256m"
#Monitor Classpath
DISTRIB=${INDIKTOR_SUITE_HOME}
MONIT_CLASSPATH_FILE=${INDIKTOR_SUITE_HOME}/component/monitor-server/bin/classpath.sh
. $MONIT_CLASSPATH_FILE
MONIT_CLASSPATH=$CLASSPATH


#System Agent default configuration
SYSTEM_AGENT_CLASSPATH_CLIENT=${SYSTEM_AGENT_HOME}/bin
SYSTEM_AGENT_JVM_ARGS="-Xmx128m -Xms64m"
#SYSTEM_AGENT_CLASSPATH_FILE=${SYSTEM_AGENT_HOME}/bin/classpath.sh
#. $SYSTEM_AGENT_CLASSPATH_FILE
#SYSTEM_AGENT_CLASSPATH=$CLASSPATH
SYSTEM_AGENT_LIB_LINUX=libsigar-x86-linux.so
SYSTEM_AGENT_LIB_LINUX_I64=libsigar-amd64-linux.so
SYSTEM_AGENT_LIB_SOLARIS_AMD64=libsigar-amd64-solaris.so
SYSTEM_AGENT_LIB_SOLARIS_I32=libsigar-x86-solaris.so
SYSTEM_AGENT_LIB_SOLARIS_I64=libsigar-x86-solaris.so
eYSTEM_AGENT_LIB_SOLARIS_SPARC64=libsigar-sparc64-solaris.so
SYSTEM_AGENT_LIB_SOLARIS_SPARC32=libsigar-sparc-solaris.so 
#System Agent default configuration : END

#Tomcat default configuration
TOMCAT_ROOT_HOME="${INDIKTOR_ROOT_HOME}/apps/tomcat"
TOMCAT_BIN="${TOMCAT_ROOT_HOME}/bin"
CATALINA_HOME="${TOMCAT_ROOT_HOME}"
CATALINA_PID=${TOMCAT_BIN}/catalina.pid
CATALINA_OUT=${TOMCAT_ROOT_HOME}/logs/catalina.out
LOGGING_CONFIG=-Djava.util.logging.config.file="${TOMCAT_ROOT_HOME}/conf/logging.properties"
JAVA_TOMCAT="${JAVA_HOME}/jre/bin/java"
JAVA_OPTS=-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager
JAVA_ENDORSED_DIRS="${TOMCAT_ROOT_HOME}/endorsed"
CLASSPATH_TOMCAT="${TOMCAT_BIN}/bootstrap.jar"
CATALINA_TMPDIR=${TOMCAT_ROOT_HOME}/temp
#Tomcat default configuration : end


#----------------------------------
#
# MAIN
#
#----------------------------------

# Checking parameters.
if [ $# -eq 0 ]; then
 usage 
 exit 0
fi

GetOptions $*
#echo " in start: ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file}"
./scripts/indiktor_lock.sh ${INDIKTOR_PROGRESS_DIR} ${_in_progress_file} instance ${INDIKTOR_ROOT_HOME}

_remaining_line=`grep -cve '^\s*$' ${INDIKTOR_PROGRESS_DIR}/${_in_progress_file}`
_instance=`sed -n '1p' ${INDIKTOR_PROGRESS_DIR}/${_in_progress_file} | awk -F: '{print $2}'`
_curr_instance=`pwd`

if [ ! -z $_remaining_line ]; then

	if [ $_instance != $_curr_instance ] && [ $_remaining_line -gt 1 ]; then 
		echo "****************************************************"
		echo "**********************  LOCK   *********************"
		echo "The indiktor instance $_instance has already started!"
		echo "****************************************************"
		echo "****************************************************"
	else
#		fn_activemq
		fn_compute_server
		fn_system_agent_server
#		fn_tomcat
		fn_monitor_server
	fi
fi


