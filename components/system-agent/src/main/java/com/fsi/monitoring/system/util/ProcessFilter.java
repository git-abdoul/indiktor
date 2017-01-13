package com.fsi.monitoring.system.util;

import java.io.Serializable;
import java.util.Collection;

public interface ProcessFilter extends Serializable {

	public void filter(Collection processes);
	
	public boolean hasProcesses();
	
	public boolean hasNetworks();
	
	public boolean hasSystemFiles();
}
