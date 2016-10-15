package com.fsi.monitoring.datamodel.ikrDefinition.expandableTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.fsi.monitoring.component.expandableTable.TableRecordBean;
import com.fsi.monitoring.kpi.compute.MetricCompute;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.kpi.units.IkrUnitType;
import com.fsi.monitoring.util.StyleBean;


public class IkrDefinitionTableRecordBean 
extends TableRecordBean {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 7608795079144046887L;

	private static RecordComparator recordComparator = new RecordComparator();

	public IkrDefinitionTableRecordBean(String indentStyleClass,
            			   				String rowStyleClass,
            			   				StyleBean styleBean,
            			   				String expandImage,
            			   				String contractImage,
            			   				ArrayList<TableRecordBean> tableData,
            			   				boolean isExpanded) {
    	super(indentStyleClass,rowStyleClass,styleBean,expandImage,contractImage,tableData,isExpanded);
    }
    
    public IkrDefinitionTableRecordBean(String indentStyleClass,
    					   				String rowStyleClass,
    					   				StyleBean styleBean,
    					   				String spacerImage) {
    	super(indentStyleClass,rowStyleClass,styleBean,spacerImage);
    }    

	protected long	 id;
    
	protected IkrCategory ikrCategory;
	protected IkrDefinition ikrDefinition;
	protected MetricCompute metricCompute;
	
	protected boolean enable = true;


    public String getName() {
    	if (ikrDefinition != null) {
    		return ikrDefinition.getIkrInstance();
    	} else {
    		return ikrCategory.getLabel();
    	}
        
    }
    
    public void setName(String name) {
    	if (ikrDefinition != null) {
    		ikrDefinition.setIkrInstance(name);
    	}
    }    
    
    public void setIkrCategory(IkrCategory ikrCategory) {
    	this.ikrCategory = ikrCategory;
    }
    
    public IkrCategory getIkrCategory() {
    	return ikrCategory;
    }
    
    public void setIkrDefinition(IkrDefinition ikrDefinition) {
    	this.ikrDefinition = ikrDefinition;
    }    
    
    public IkrDefinition getIkrDefinition() {
    	return ikrDefinition;
    }

    public void setMetricCompute(MetricCompute metricCompute) {
    	this.metricCompute = metricCompute;
    }
    
    public MetricCompute getMetricCompute() {
    	return metricCompute;
    }
    
    public String getComputedValue() {
    	String res = "";
    	if (metricCompute!=null)
    		res = metricCompute.name();
    	return res;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getEnable() {
        return enable;
    }
    
    public boolean isComputesRendered() {
    	if (ikrDefinition != null && ikrDefinition.getIkrCompute() != null) {
    		return false;
    	}
    	
    	IkrUnitType ikrUnitType = null;
    	if (ikrCategory != null) {
    		ikrUnitType = ikrCategory.getIkrUnitType();
    	}
     	return !(ikrUnitType == null || ikrUnitType.getSupportedComputes() == null || ikrUnitType.getSupportedComputes().size() == 0);
    }
    
    public String getComputePanelStack() {
    	String res = "NO_COMPUTE";
    	
    	IkrUnitType ikrUnitType = null;
    	Collection<MetricCompute> computes = new ArrayList<MetricCompute>();
    	if (ikrCategory != null) {
    		ikrUnitType = ikrCategory.getIkrUnitType();
    		if (ikrUnitType != null)
    			computes = ikrUnitType.getSupportedComputes();
    	}
    	
    	if (ikrDefinition != null) {
    		if (!MetricCompute.RT.equals(ikrDefinition.getIkrCompute()) && computes!=null && computes.contains(metricCompute)) {
    			res = "COMPUTED";
    		}
    		else if (MetricCompute.RT.equals(ikrDefinition.getIkrCompute()) && computes != null && computes.size()>0){
    			res = "STATISTIC_COMPUTE";
    		}
    	}
    	
    	return res;
    }
    
    public int getIkrCategoryId() {
    	return ikrCategory != null ? ikrCategory.getId() : 0;
    }
    
    public Collection<SelectItem> getMetricComputeItems() {
    	Collection<SelectItem> res = new ArrayList<SelectItem>();
    	
    	IkrUnitType ikrUnitType = null;
    	if (ikrCategory != null) {
    		ikrUnitType = ikrCategory.getIkrUnitType();
    	}
    	
    	if (ikrUnitType != null) {
	    	Collection<MetricCompute> computes = ikrUnitType.getSupportedComputes();
	    	
	    	if (computes != null && computes.size() > 0) {    		
	        	for (MetricCompute compute : computes) {
	        		SelectItem item = new SelectItem(compute, compute.name());
	        		res.add(item);
	        	}
	    	}
    	}
    	return res;
    }    

    public void setEnable(boolean enable) {}
    
    public void modifyEnable(boolean enable) {
    	 this.enable = enable;
    }
    
    public void select(ValueChangeEvent  event) {
    	Boolean newValue = (Boolean)event.getNewValue();
    	this.enable = newValue;
        if (childFilesRecords != null && childFilesRecords.size() > 0) {	
        	for (TableRecordBean recordBean : childFilesRecords) {
        		IkrDefinitionTableRecordBean ikrRecordBean = (IkrDefinitionTableRecordBean)recordBean;
        		ikrRecordBean.modifyEnable(enable);
        	}
        }   
       	
        IkrDefinitionTableRecordBean parentBean = (IkrDefinitionTableRecordBean)parent;
        if (parentBean != null && newValue == true) {
    		 
    		 parentBean.modifyEnable(newValue);
    	 }
    }

	@Override
	protected ArrayList<TableRecordBean> getSortedChildFilesRecords() {
	//	ArrayList<TableRecordBean> records = super.getChildFilesRecords();
		if (childFilesRecords != null && childFilesRecords.size() > 1) {
			Collections.sort(childFilesRecords, recordComparator);
		}
		return childFilesRecords;
	}
	
	
	private static class RecordComparator implements Comparator<TableRecordBean> {
		public int compare(TableRecordBean o1, TableRecordBean o2) {
			IkrDefinitionTableRecordBean rb1 = (IkrDefinitionTableRecordBean)o1;
			IkrDefinitionTableRecordBean rb2 = (IkrDefinitionTableRecordBean)o2;
			return rb1.getName().compareTo(rb2.getName());
		}	
	}
}