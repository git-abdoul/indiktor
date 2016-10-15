package com.fsi.monitoring.alert.composite;

import java.io.Serializable;

import com.fsi.monitoring.util.MessageBundleLoader;

public class AlertItem
implements Serializable {

	private static final long serialVersionUID = 1716282178439468559L;

	public static final int NO_ALERT_DEFINITION 	= -1;
	public static final int ALERT_IDLE_DEFINITION 	= 0;	
	
	private String title;
	private String type;
	
	protected int level;
	
	public AlertItem(String title,
					 String type) {
		this.title = title;
		this.type = type;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getType() {
		return type;
	}
	
	public String getUrl() {
		if (type != null) {
			if (type.equals("wfwrule")) {
				return "/" + MessageBundleLoader.getMessage("alert.wfw.img." + level);
			} else if (type.equals("batchmode")) {
				return "/" + MessageBundleLoader.getMessage("alert.mode.img." + level);
			} else if (type.equals("down")) {
				return "/" + MessageBundleLoader.getMessage("alert.down.img." + level);
			} else if (type.equals("trend")) {
				return "/" + MessageBundleLoader.getMessage("trend." + level);
			} else if (type.equals("status")) {
				return "/" + MessageBundleLoader.getMessage("task.status." + level);
			} else if (type.equals("engine")) {
				return "/" + MessageBundleLoader.getMessage("task.status." + level);
			}
		}
		return "/" + MessageBundleLoader.getMessage("alert.img." + level);
	}
	
	public int getLevel() {
		return level;
	}
}
