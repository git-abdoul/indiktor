package com.fsi.monitoring.datamodel.bean;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;

public class IkrDefinitionBean
extends MetricGroupBean
implements Serializable {
	private static final Logger logger = Logger.getLogger(IkrDefinitionBean.class);	

	private static final long serialVersionUID = 7558480525970257512L;
	
	private AbstractIkrDefinition ikrDefinition = null;
	
	private BeanPM beanPM;
	
	public IkrDefinitionBean(AbstractIkrDefinition ikrDefinition,
							 IkrCategory ikrCategory,
							 String context,
							 LogicalEnv logicalEnv,
							 String domainView) {
		super(ikrCategory,context,logicalEnv, domainView);
		this.ikrDefinition = ikrDefinition;
	}
	
	public IkrDefinitionBean(AbstractIkrDefinition ikrDefinition,
							 IkrCategory ikrCategory) {
		super(ikrCategory);
		this.ikrDefinition = ikrDefinition;
	}	
	
	public String getInstance() {
		String res = "";
		if (ikrDefinition != null)
			res = ikrDefinition.getIkrInstance();
		else
			res = getIkrCategory().getLabel();
		return res;
	}
	
	public int getId() {
		int id = 0;
		if (ikrDefinition != null)
			id = (int)ikrDefinition.getId();
		return id;
	}
	
	public AbstractIkrDefinition getIkrDefinition() {
		return ikrDefinition;
	}
	
	public int hashCode() {
		return (int)ikrDefinition.getId();
	}
	
	 public boolean equals(Object obj) { 
		 IkrDefinitionBean other = (IkrDefinitionBean)obj;
		 boolean res = false;
		 if (ikrDefinition != null && other.getIkrDefinition()!=null)
			 res = (this.ikrDefinition.getId() == other.getIkrDefinition().getId());
		 else
			 super.equals(obj);
		 return res;
	 }
	 
	 public String getFullIkrInstance() {
		return ikrDefinition.getFullIkrInstance();
	}	
}
