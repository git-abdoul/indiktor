package com.fsi.monitoring.datamodel.selection;

import javax.faces.model.SelectItem;

public class SelectionTagsBean {

	
    private static final SelectItem[] MONITOR_TYPES = new SelectItem[]{
        new SelectItem("CACHE_MONITOR"),
        new SelectItem("EVENT_MONITOR"),
        new SelectItem("WORKFLOW_MONITOR"),
        new SelectItem("JMX_MONITOR"),
        new SelectItem("JSTAT_MONITOR"),
        new SelectItem("PROCESS_MONITOR"), 
        new SelectItem("RSTATD_MONITOR"),
        new SelectItem("OPMON_MONITOR"), 
        new SelectItem("SYSTEM_MONITOR"), 
        new SelectItem("SYBASE_MONITOR")};
}
