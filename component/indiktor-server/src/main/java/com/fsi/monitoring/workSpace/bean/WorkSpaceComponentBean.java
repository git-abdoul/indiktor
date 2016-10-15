package com.fsi.monitoring.workSpace.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class WorkSpaceComponentBean 
implements Serializable {

	private static final long serialVersionUID = -8521706153620521379L;
	
	public enum Type {grid,graph};
	
	private String wsId;
	private String title;
	private Type type;
	private Date fromDate;
	private Date toDate;
	
	private SimpleDateFormat dateFormat;
	
	public WorkSpaceComponentBean(String wsId, Type type) {
		this.wsId = wsId;
		this.type = type;
		dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	}	
	
	public String getWsId() {
		return wsId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isGraphComponent() {
		return type == Type.graph;
	}	
	
	public boolean isGridComponent() {
		return type == Type.grid;
	}
	
	public Date getFromDate() {
		return fromDate;
	}
	
	public String getFromDateStr() {		
		return dateFormat.format(fromDate);
	}
	
	public String getToDateStr() {
		return dateFormat.format(toDate);
	}
	
	public void setFromDate(Date date) {
		this.fromDate = date;
	}
	
	public Date getToDate() {
		return toDate;
	}
	
	public void setToDate(Date date) {
		this.toDate = date;
	}
}
