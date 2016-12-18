/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.fsi.monitoring.component.expandableTable;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.StyleBean;

public abstract class TableRecordsManager 
extends AccessControlBean  {

	private static final long serialVersionUID = -701571104579987329L;

	private static final Logger logger = Logger.getLogger(TableRecordsManager.class);
	
	protected ArrayList<TableRecordBean> recordBeans;
    private boolean isInit;
    
    protected StyleBean styleBean = null;

    // css style related constants
    protected static final String GROUP_INDENT_STYLE_CLASS = "groupRowIndentStyle";
    protected static final String GROUP_ROW_STYLE_CLASS = "groupRowStyle";
    protected static final String CHILD_INDENT_STYLE_CLASS = "childRowIndentStyle";
    protected static final String CHILD_ROW_STYLE_CLASS = "childRowStyle";
    
    // toggle for expand contract
    protected static final String CONTRACT_IMAGE = "tree_nav_top_close_no_siblings.gif";
    protected static final String EXPAND_IMAGE = "tree_nav_top_open_no_siblings.gif";
    protected static final String SPACER_IMAGE = "tree_line_blank.gif";
    
    public TableRecordsManager() {
    	
    }

    public void init() {
        isInit = true;

        // initiate the list
        if (recordBeans != null) {
        	recordBeans.clear();
        } else {
        	recordBeans = new ArrayList<TableRecordBean>();
        }

        try {
        	styleBean = (StyleBean)FacesUtils.getManagedBean("styleBean");
        } catch (Exception exc) {
        	logger.error("loading styleBean failed in TableRecordsManager",exc);
        }
        	
        initTableData();
    }
 
    protected abstract void initTableData();

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