package com.fsi.monitoring.system.dto;

import java.util.ArrayList;
import java.util.List;

public class HostProcessInfoList extends SystemInfo {
	private static final long serialVersionUID = 70172823292994055L;

	/**
	 * @uml.property  name="list"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="com.fsi.monitoring.system.dto.HostProcessInfo"
	 */
	private List<HostProcessInfo> list;
	
	protected HostProcessInfoList() {
		super("ProcessInfoList");
		this.list = new ArrayList<HostProcessInfo>();
	}
	
	public int getSize() {
		return list.size();
	}
	
	public void add(HostProcessInfo hostProcessInfo) {
		this.list.add(hostProcessInfo);
	}
	
	public List<HostProcessInfo> getList(){
		return list;
	}
}
