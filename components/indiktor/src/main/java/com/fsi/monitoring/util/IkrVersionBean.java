package com.fsi.monitoring.util;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.ikr.model.IkrVersion;
import com.fsi.monitoring.indiktor.DataModelPM;

public class IkrVersionBean {
	private static final Logger logger = Logger.getLogger(IkrVersionBean.class);	
	
	private IkrVersion version;
	
	public String getLabel() {
		String label = "";
		if (version != null)
			label = version.getFullVersion();
		return label;
	}

	public void setDataModelPM(DataModelPM dataModelPM) {
		if (version != null)
			return;
		
		try {
			version = dataModelPM.getIkrVersion();
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
