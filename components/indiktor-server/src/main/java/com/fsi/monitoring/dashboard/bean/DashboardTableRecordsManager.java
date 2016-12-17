package com.fsi.monitoring.dashboard.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

import com.fsi.monitoring.component.expandableTable.TableRecordBean;
import com.fsi.monitoring.util.MessageBundleLoader;
import com.fsi.monitoring.util.StyleBean;

public abstract class DashboardTableRecordsManager 
implements Serializable {

	private static final long serialVersionUID = -701571104579987329L;

	protected ArrayList<TableRecordBean> recordBeans;
    private boolean isInit;
    
    protected StyleBean styleBean = null;

    // css style related constants
    protected static final String GROUP_INDENT_STYLE_CLASS = MessageBundleLoader.getMessage("expandTable.default.GROUP_INDENT_STYLE_CLASS");
    protected static final String GROUP_ROW_STYLE_CLASS = MessageBundleLoader.getMessage("expandTable.default.GROUP_ROW_STYLE_CLASS");
    protected static final String CHILD_INDENT_STYLE_CLASS = MessageBundleLoader.getMessage("expandTable.default.CHILD_INDENT_STYLE_CLASS");
    protected static final String CHILD_ROW_STYLE_CLASS = MessageBundleLoader.getMessage("expandTable.default.CHILD_ROW_STYLE_CLASS");
    
    // toggle for expand contract
    protected static final String CONTRACT_IMAGE = MessageBundleLoader.getMessage("expandTable.default.CONTRACT_IMAGE");
    protected static final String EXPAND_IMAGE = MessageBundleLoader.getMessage("expandTable.default.EXPAND_IMAGE");
    protected static final String SPACER_IMAGE = MessageBundleLoader.getMessage("expandTable.default.SPACER_IMAGE");
 
    protected abstract void initTableData(Properties resource);
    

    /**
     * Cleans up the resources used by this class.  This method could be called
     * when a session destroyed event is called.
     */
    public void dispose() {
        isInit = false;
        // clean up the array list
        if (recordBeans != null) {
            TableRecordBean tmp;
            ArrayList<TableRecordBean> tmpList;
            for (int i = 0; i < recordBeans.size(); i++) {
                tmp = recordBeans.get(i);
                tmpList = tmp.getChildFilesRecords();
                if (tmpList != null) {
                    tmpList.clear();
                }
            }
            recordBeans.clear();
        }
    }

    /**
     * Gets the list of FilesGroupRecordBean which will be used by the
     * ice:dataTable component.
     *
     * @return array list of parent FilesGroupRecordBeans
     */
    public ArrayList<TableRecordBean> getFilesGroupRecordBeans() {
        return recordBeans;
    } 
}