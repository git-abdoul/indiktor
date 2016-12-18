package com.fsi.monitoring.report.tree.userObject;

import java.util.Collection;

import javax.swing.tree.DefaultMutableTreeNode;

import com.fsi.monitoring.config.PersistencyBeanName;

import com.fsi.monitoring.report.ReportConfig;
import com.fsi.monitoring.report.ReportPM;
import com.fsi.monitoring.report.ReportType;
import com.fsi.monitoring.util.FacesUtils;

public class ReportTypeObject
extends AbstractObject {
	
	private ReportType reportType;

	public ReportTypeObject(ReportType reportType,DefaultMutableTreeNode wrapper) {
		super(wrapper);
		this.reportType = reportType;
	}	

	@Override
	public void setExpanded(boolean isExpanded) {
		super.setExpanded(isExpanded);
		if (!loaded) {
			try {
				ReportPM reportPM = (ReportPM)FacesUtils.getManagedBean(PersistencyBeanName.reportPM.name());
				Collection<ReportConfig> reportConfigs =  reportPM.getReportConfigs(reportType.getId());
				       
			    //DefaultMutableTreeNode monitorRootNode = wrapper;
			        
			    for (ReportConfig reportConfig : reportConfigs) {		        	
	
			        DefaultMutableTreeNode branchNode = new DefaultMutableTreeNode();
			        ReportConfigObject branchObject = new ReportConfigObject(reportType,
			        														 reportConfig,
			        														 branchNode);
			        branchNode.setUserObject(branchObject);
			        branchObject.setLeaf(true);
			        wrapper.add(branchNode);
				}
			    loaded = true;
	    	} catch(Exception exc) {
	    		System.out.println(exc);
	    	}
		}
		
		if (isExpanded) {
			//selectNodeObject(null);
		}
	}	
	
	
	@Override
	public String getText() {
		return reportType.getName();
	}
	

	
}
