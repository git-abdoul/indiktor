package com.fsi.monitoring.workSpace.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;

public class WorkSpaceGridBean 
extends WorkSpaceComponentBean {

	private static final long serialVersionUID = -3874928846974326928L;

	private IkrDefinitionBean ikrDefinitionBean;
	private List<IkrValueBean> valueBeans;	

	private GridSorter gridSorter;	
	
	public WorkSpaceGridBean(String wsId, Collection<IkrValueBean> valueBeans) {
		super(wsId,Type.grid);
		this.valueBeans = (List)valueBeans;
		
		gridSorter = new GridSorter("Date");
		gridSorter.sort();
	}
	
	public WorkSpaceGridBean(String title) {
		this(title,new ArrayList<IkrValueBean>());
	}	
	
	public GridSorter getGridSorter() {
		return gridSorter;
	}
	
	public Collection<IkrValueBean> getIkrValueBeans() {
		return valueBeans;
	}
	
	public void addIkrValueBean(IkrValueBean ikrValueBean) {
		valueBeans.add(ikrValueBean);
	}
	
	public boolean isAlive() {
		return valueBeans != null || !valueBeans.isEmpty();
	}

	public AbstractIkrDefinition getIkrDefinition() {
		return ikrDefinitionBean.getIkrDefinition();
	}
	
	public IkrCategory getIkrCategory() {
		return ikrDefinitionBean.getIkrCategory();
	}
	
	public String getContext() {
		return ikrDefinitionBean.getContext();
	}

	public void setIkrDefinitionBean(IkrDefinitionBean ikrDefinitionBean) {
		this.ikrDefinitionBean = ikrDefinitionBean;
	}
	
	public class GridSorter
	extends SortableList {
		
		protected GridSorter(String defaultSortColumn) {
			super(defaultSortColumn);
			comparator = new IkrValueBeanComparator();
		}

		@Override
		protected boolean isDefaultAscending(String sortColumn) {
			return false;
		}

		@Override
		protected void sort() {
			Collections.sort(valueBeans, comparator);
		}
		
		private class IkrValueBeanComparator
		implements Comparator<IkrValueBean> {

			public int compare(IkrValueBean o1,
							   IkrValueBean o2) {
		        if (sortColumnName.equals("Date")) {
		            return ascending ? 
		            	o1.getIkrValue().getCaptureTime().compareTo(o2.getIkrValue().getCaptureTime()) :
		            	o2.getIkrValue().getCaptureTime().compareTo(o1.getIkrValue().getCaptureTime());
		        } else if (sortColumnName.equals("Value")) {
		        	return ascending ?
		        		o1.getIkrValue().getValue().compareTo(o2.getIkrValue().getValue()) :
		        		o2.getIkrValue().getValue().compareTo(o1.getIkrValue().getValue());	
		        }
				return 0;
			}
		}
	}
}
