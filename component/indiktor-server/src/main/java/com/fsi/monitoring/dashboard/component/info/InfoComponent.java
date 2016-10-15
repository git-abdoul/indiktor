package com.fsi.monitoring.dashboard.component.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.datamodel.bean.RealTimeBean;
import com.fsi.monitoring.datamodel.bean.StaticDataDefinitionBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.compute.MetricCompute;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.msd.StaticData;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponent;

public class InfoComponent 
extends DashBoardComponent
implements RealTimeComponent {
	private static final long serialVersionUID = -8452720184903009237L;

	private static final Logger logger = Logger.getLogger(InfoComponent.class);
	
	private Map<Long, InfoDetail> infoMap;
	
    private String columnName = "Name";	
	private boolean ascending = true;
	
	private BeanPM beanPM;
	
	public InfoComponent(String componentId,
						 String title,
						 String style,
					  	 BeanPM beanPM) {
		super(componentId,title,style, "infoBoard", true);		
		infoMap = new HashMap<Long,InfoDetail>();
		this.beanPM = beanPM;
	}
	
	public void setInfo(long ikrDefinitionId, String label, MonitoringPM monitoringPM, DataModelPM dataModelPM) {		
		InfoDetail infoDetail = new InfoDetail(label);
		try {
			AbstractIkrDefinition ikrDefinition =  monitoringPM.getIkrDefinition(ikrDefinitionId);
			if (ikrDefinition!=null && MetricCompute.STATIC.equals(ikrDefinition.getIkrCompute())) {
				StaticData sd = (StaticData)ikrDefinition;
				LogicalEnv logicalEnv = dataModelPM.getLogicalEnv(sd.getLogicalEnvId());
				IkrCategory metricCategory = (IkrCategory)dataModelPM.getIkrStaticDomain(ikrDefinition.getIkrCategoryId());
				IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(metricCategory.getParentDomainId());
				StaticDataDefinitionBean sdBean = new StaticDataDefinitionBean(logicalEnv, ikrDefinition, metricDomain, metricCategory);
				IkrValue ikrValue = new IkrValue();
				ikrValue.setCaptureTime(new Date());
				ikrValue.setIkrCategoryId(metricCategory.getId());
				ikrValue.setIkrDefinitionId(ikrDefinition.getId());
				ikrValue.setValue(((StaticData)ikrDefinition).getValue());
				IkrValueBean ikrValueBean = new IkrValueBean(sdBean, ikrValue);
				infoDetail.setIkrValueBean(ikrValueBean);
			}
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
		infoMap.put(ikrDefinitionId, infoDetail);
	}
	
	@Override
	public void synchronize() {
		for (long ikrDefId : infoMap.keySet()) {
			try {
				List<IkrValueBean> valueBeans = beanPM.getLastIkrValuesBeanByIkrDefinition(ikrDefId, 1);
				for (IkrValueBean bean : valueBeans) {
					push(bean);
				}
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}
		}		
	}
	
	public void push(RealTimeBean valueBean) {
		IkrValueBean ikrValueBean = (IkrValueBean)valueBean;
		long ikrDefinitionId = ikrValueBean.getIkrDefinitionBean().getIkrDefinition().getId();
			
		InfoDetail infoDetail = infoMap.get(ikrDefinitionId);
			
		if (infoDetail != null) {
			infoDetail.setIkrValueBean(ikrValueBean);
		}	
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}
	
	public Collection<InfoDetail> getInfoDetails() {
		List<InfoDetail> infos = new ArrayList<InfoDetail>(infoMap.values());
		if (infos != null && infos.size()>0) {
			Collections.sort(infos, new Comparator<InfoDetail>() {
				public int compare(InfoDetail o1, InfoDetail o2) {
					Integer res = 0;					
					if ("Name".equals(columnName))
						res = ascending ? o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase()) :  o2.getLabel().toLowerCase().compareTo(o1.getLabel().toLowerCase());
					else if ("Value".equals(columnName))
						res = ascending ? o1.getValue().toLowerCase().compareTo(o2.getValue().toLowerCase()) :  o2.getValue().toLowerCase().compareTo(o1.getValue().toLowerCase());
					return res;
				}
			});
		}
		return infos;
	}
	
	public Collection<Long> getIkrDefintionIds() {
		return infoMap.keySet();
	}
	
	public String getScrollHeight() {
		List<InfoDetail> infos = new ArrayList<InfoDetail>(infoMap.values());
		if(infos.size() > 10)
			return "270px";
		else
			return "auto";
	}
}
