package com.fsi.monitoring.alert.config;

import java.util.Comparator;
import java.util.Date;

import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.config.definition.AlertDefinitionBean;
import com.fsi.monitoring.component.table.SortableList;

public abstract class AlertDefinitionSortableList
extends SortableList {
	
	protected static final String labelColumnName = "Label";
	protected static final String creationDateColumnName = "Creation Date";
	protected static final String lastModifyDateColumnName = "Last Modify Date";
	protected static final String envColumnName = "Environment";
	protected static final String groupColumnName = "Group";
	protected static final String domainColumnName = "Domain";
	protected static final String subDomainColumnName = "Sub Domain";
	
	protected AlertDefinitionSortableList(String defaultSortColumn) {
		super(defaultSortColumn);
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		 return true;
	}
	
	public String getLabelColumnName() {
		return labelColumnName;
	}	
	
	public String getCreationDateColumnName() {
		return creationDateColumnName;
	}	
	
	public String getLastModifyDateColumnName() {
		return lastModifyDateColumnName;
	}	
	
	public String getGroupColumnName() {
		return groupColumnName;
	}

	public String getDomainColumnName() {
		return domainColumnName;
	}

	public String getSubDomainColumnName() {
		return subDomainColumnName;
	}

	public String getEnvironmentColumnName() {
		return envColumnName;
	}	

	public class AlertDefinitionComparator
	implements Comparator<AlertDefinitionBean>  {
	    public int compare(AlertDefinitionBean ad1, AlertDefinitionBean ad2) {
	    	AlertDefinition o1 = ad1.getAlertDefinition();
	    	AlertDefinition o2 = ad2.getAlertDefinition();
	
	        if (getLabelColumnName().equals(getSortColumnName())) {
	        	String label1 = o1.getName();
	        	String label2 = o2.getName();
	            return ascending ? label1.compareTo(label2) : label2.compareTo(label1);
	        } else if (getCreationDateColumnName().equals(getSortColumnName())) {
	        	Date d1 = o1.getCreationDate();
	        	Date d2 = o2.getCreationDate();
	        	return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
	        } else if (getLastModifyDateColumnName().equals(getSortColumnName())) {
	        	Date d1 = o1.getLastUpdateDate();
	        	Date d2 = o2.getLastUpdateDate();
	        	return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
	        } else if (getGroupColumnName().equals(getSortColumnName())) {
	        	String g1 = ad1.getGroup();
	        	String g2 = ad2.getGroup();
	            return ascending ? g1.compareTo(g2) : g2.compareTo(g1);
	        } else if (getDomainColumnName().equals(getSortColumnName())) {
	        	String d1 = ad1.getDomain();
	        	String d2 = ad2.getDomain();
	            return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
	        } else if (getSubDomainColumnName().equals(getSortColumnName())) {
	        	String sd1 = ad1.getSubDomain();
	        	String sd2 = ad2.getSubDomain();
	            return ascending ?  sd1.compareTo(sd2) : sd2.compareTo(sd1);
	        } else if (getEnvironmentColumnName().equals(getSortColumnName())) {	        	
	        	String label1 = ad1.getLogicalEnv().getName();
	        	String label2 = ad1.getLogicalEnv().getName();
	            return ascending ? label1.compareTo(label2) : label2.compareTo(label1);
	        } else return 0;
	    }
	};
}
