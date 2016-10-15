package com.fsi.monitoring.report.tree;


import java.io.Serializable;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.report.ReportPM;
import com.fsi.monitoring.report.ReportType;
import com.fsi.monitoring.report.tree.userObject.ReportTypeObject;
import com.fsi.monitoring.util.FacesUtils;


public class TreeController 
implements Serializable {
	 
	private static final long serialVersionUID = -7350454623722697350L;
	
	private DefaultTreeModel model;
    
    public DefaultTreeModel getModel() {
        return model;
    }
    
	public void init() {
    	try {	    	
	        // create root node with its children expanded
	        DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode();
//	        RootObject rootObject = new RootObject(rootTreeNode);
//	        rootObject.setText("Reports");
//	        rootObject.setExpanded(true);
//	        rootTreeNode.setUserObject(rootObject);
	
	        // model is accessed by by the ice:tree component
	        model =  new DefaultTreeModel(rootTreeNode);
	        
			ReportPM reportPM = (ReportPM)FacesUtils.getManagedBean(PersistencyBeanName.reportPM.name());
	        List<ReportType> reports = reportPM.getReportTypes();
	        
	        // add some child notes
	        for (ReportType reportType : reports) {

	            DefaultMutableTreeNode branchNode = new DefaultMutableTreeNode();
	            ReportTypeObject branchObject = new ReportTypeObject(reportType,branchNode);
	            branchNode.setUserObject(branchObject);
	            branchObject.setLeaf(false);
	            rootTreeNode.add(branchNode);
	        }
        
    	} catch(Exception exc) {
    		System.out.println(exc);
    	}
    }
}
