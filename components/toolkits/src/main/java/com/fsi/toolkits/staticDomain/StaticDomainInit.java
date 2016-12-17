package com.fsi.toolkits.staticDomain;

import java.io.File;

import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.toolkits.config.ToolkitContext;

public class StaticDomainInit {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Start : init Metric Domain");
			ToolkitContext.getContext().init("applicationContext-toolkits.xml", "toolkits");			
			String toolkitsResourceHome = System.getProperty("toolkit.resources");
			String staticDomainModelFileName = toolkitsResourceHome + File.separator + "staticDomainModel.xml";
			String metricDomainConfigFileName = toolkitsResourceHome + File.separator + "metricDomainConfig.xml";		
			DataModelPM dataModelPM = (DataModelPM) ToolkitContext.getBean(PersistencyBeanName.dataModelPM.name());		
			System.out.println("Feeding static domain ...");
			dataModelPM.initIkrStaticDomains(staticDomainModelFileName, metricDomainConfigFileName);	
			System.out.println("End : init Metric Domain");
		} catch(Exception exc) {
			System.out.println(exc.getMessage());
			exc.printStackTrace();			
		}
		finally {
			System.exit(0);
		}
		
		

	}

}
