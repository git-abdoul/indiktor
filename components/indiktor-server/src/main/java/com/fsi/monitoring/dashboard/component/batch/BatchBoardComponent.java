package com.fsi.monitoring.dashboard.component.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.datamodel.bean.RealTimeBean;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponent;

public class BatchBoardComponent
extends DashBoardComponent
implements RealTimeComponent {
	private static final long serialVersionUID = 5296415590615471737L;
	
	private static final Logger logger = Logger.getLogger(BatchBoardComponent.class);
	
	private Map<String, BatchBoardDetail> batchBoardMap;
	
	private List<BatchBoardDetail> batchBoardDetails;
	
	private List<Long> ikrDefintionIds;
	
	private int logicalEnvId;
	private String context;
	
	private String colors;
	
    private String columnName = "Name";	
	private boolean ascending = true;
	
	public BatchBoardComponent(String componentId,
									  String title,
									  String style) {
		super(componentId, title, style, "batchBoard", true);
		batchBoardMap = new HashMap<String, BatchBoardDetail>();
		ikrDefintionIds = new ArrayList<Long>();
	}
	
	public void setInfo(int logicalEnvId, String context, String ikrInstance, String batchLabel) {		
		this.logicalEnvId = logicalEnvId;
		this.context = context;
		
		BatchBoardDetail batchBoardDetail = new BatchBoardDetail((batchLabel!=null&&batchLabel.length()>0)?batchLabel:ikrInstance);
		batchBoardMap.put(ikrInstance, batchBoardDetail);
	}
	
	@Override
	public void synchronize() {
		// TODO Auto-generated method stub
		
	}
	
	protected boolean isAccepted(IkrValueBean ikrValueBean) {
		String ikrInstance = ikrValueBean.getIkrDefinitionBean().getIkrDefinition().getIkrInstance();
		int logicalEnvIdRec = ikrValueBean.getIkrDefinitionBean().getLogicalEnv().getId();
		String contextRec = ikrValueBean.getIkrDefinitionBean().getContext();
		return (batchBoardMap.containsKey(ikrInstance) && (logicalEnvId==logicalEnvIdRec && context.equals(contextRec)));
	}

	public void push(RealTimeBean valueBean) {
		IkrValueBean ikrValueBean = (IkrValueBean)valueBean;			
		if (isAccepted(ikrValueBean)) {
			String ikrInstance = ikrValueBean.getIkrDefinitionBean().getIkrDefinition().getIkrInstance();
			BatchBoardDetail detail = batchBoardMap.get(ikrInstance);
			String metricCategory = ikrValueBean.getIkrDefinitionBean().getIkrCategory().getDomainValue();
			if ("Batch Start Time".equals(metricCategory)) {				
				detail.setStartTime(ikrValueBean.getFormattedValue().getValue());
			}
			else if ("Batch End Time".equals(metricCategory)) {
				detail.setEndTime(ikrValueBean.getFormattedValue().getValue());
			}
			else if ("Batch Scheduled Time".equals(metricCategory)) {
				detail.setScheduledTime(ikrValueBean.getFormattedValue().getValue());
			}
			else if ("Batch Uptime".equals(metricCategory)) {
				detail.setUptime(ikrValueBean.getFormattedValue().getValue());
				detail.setUptimeUnit(ikrValueBean.getFormattedValue().getIkrUnit().getSymbol());
			}
			else if ("Batch Status".equals(metricCategory)) {
				detail.setStatus(ikrValueBean.getFormattedValue().getValue());

				String statusForColor = detail.getStatus();
				if(statusForColor != null && statusForColor.length()>0){
					if(statusForColor.equals("SUCCESS")) {
						detail.setColor("BatchBoardSUCCESS");
					}
					if(statusForColor.equals("FAILED")) {
						detail.setColor("BatchBoardFAILED");
					}
					if(statusForColor.equals("IN_PROGRESS")) {
						detail.setColor("BatchBoardIN_PROGRESS");
					}
					if(statusForColor.equals("NOT_STARTED")) {
						detail.setColor("BatchBoardNOT_STARTED");
					}
					if(statusForColor.equals("DELAYED")) {
						detail.setColor("BatchBoardDELAYED");
					}
				}
			}
			else if ("Batch Delay".equals(metricCategory)) {
				detail.setDelay(ikrValueBean.getFormattedValue().getValue());
			}
		}
	}
	
	public String getContext() {
		return context;
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
	
	public Collection<BatchBoardDetail> getBatchBoardDetails() {
		batchBoardDetails = new ArrayList<BatchBoardDetail>(batchBoardMap.values());
		if (batchBoardDetails != null && batchBoardDetails.size()>0) {
			Collections.sort(batchBoardDetails, new Comparator<BatchBoardDetail>() {
				public int compare(BatchBoardDetail o1, BatchBoardDetail o2) {
					Integer res = 0;					
					if ("Name".equals(columnName) && o1.getLabel() != null && o1.getLabel().length()>0 && o2.getLabel() != null && o2.getLabel().length()>0)
						res = ascending ? o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase()) :  o2.getLabel().toLowerCase().compareTo(o1.getLabel().toLowerCase());
					else if ("Scheduled Time".equals(columnName) && o1.getScheduledTime() != null && o1.getScheduledTime().length()>0 && o2.getScheduledTime() != null && o2.getScheduledTime().length()>0)
						res = ascending ? o1.getScheduledTime().toLowerCase().compareTo(o2.getScheduledTime().toLowerCase()) :  o2.getScheduledTime().toLowerCase().compareTo(o1.getScheduledTime().toLowerCase());
					else if ("Start Time".equals(columnName) && o1.getStartTime() != null && o1.getStartTime().length()>0 && o2.getStartTime() != null && o2.getStartTime().length()>0)
						res = ascending ? o1.getStartTime().toLowerCase().compareTo(o2.getStartTime().toLowerCase()) :  o2.getStartTime().toLowerCase().compareTo(o1.getStartTime().toLowerCase());
					else if ("End Time".equals(columnName) && o1.getEndTime() != null && o1.getEndTime().length()>0 && o2.getEndTime() != null && o2.getEndTime().length()>0)
						res = ascending ? o1.getEndTime().toLowerCase().compareTo(o2.getEndTime().toLowerCase()) :  o2.getEndTime().toLowerCase().compareTo(o1.getEndTime().toLowerCase());
					else if ("Uptime".equals(columnName) && o1.getUptime() != null && o1.getUptime().length()>0 && o2.getUptime() != null && o2.getUptime().length()>0)
						res = ascending ? o1.getUptime().toLowerCase().compareTo(o2.getUptime().toLowerCase()) :  o2.getUptime().toLowerCase().compareTo(o1.getUptime().toLowerCase());
					else if ("Status".equals(columnName) && o1.getStatus() != null && o1.getStatus().length()>0 && o2.getStatus() != null && o2.getStatus().length()>0)
						res = ascending ? o1.getStatus().toLowerCase().compareTo(o2.getStatus().toLowerCase()) :  o2.getStatus().toLowerCase().compareTo(o1.getStatus().toLowerCase());
					return res;
				}
			});
		}
		colors = "";
		int i = 0;
		for (BatchBoardDetail batchBoardDetail : batchBoardDetails) {
			if(i == 0) {
				if(batchBoardDetail.getColor() != null)
					colors = batchBoardDetail.getColor();
				else
					colors = "BatchBoardSTATUSNULL";
			}
			else {
				if(batchBoardDetail.getColor() != null)
					colors = colors + ", " + batchBoardDetail.getColor();
				else
					colors = colors + ", BatchBoardSTATUSNULL";
			}
			i++;
		}
		return batchBoardDetails;
	}

	public String getColors() {
		return colors;
	}

	public void setColors(String color) {
		this.colors = colors;
	}

	public int getLogicalEnvId() {
		return logicalEnvId;
	}
	
	public void addIkrdefinitionId(long id) {
		ikrDefintionIds.add(id);
	}

	public List<Long> getIkrDefintionIds() {
		return ikrDefintionIds;
	}

	public void setIkrDefintionIds(List<Long> ikrDefintionIds) {
		this.ikrDefintionIds = ikrDefintionIds;
	}	
	
	
}
