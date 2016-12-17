package com.fsi.monitoring.dashboard.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import java.util.HashSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import com.fsi.monitoring.dashboard.component.DashBoardSummaryComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponents;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;


import generated.dashboard.config.schema.NavigationBoardType.NavigationItemType;

public class NavigationItemConfig 
implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9024889522210252826L;

	private static final Logger logger = Logger.getLogger(NavigationItemConfig.class);	
	
	private NavigationItemType itemType;
	
    private SelectItem[] environmentItems;
    private SelectItem[] typeItems;
    private SelectItem[] titleItems;
	
    private Collection<DashBoardSummaryComponent> summaries;    
    
    private DashBoardComponents components;
	
	public NavigationItemConfig(NavigationItemType itemType){
		this.itemType = itemType;
	}
	
	public void init() {
		summaries = new ArrayList<DashBoardSummaryComponent>();
		
		try {
			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
	
			Collection<DashBoardComponents> components = factory.getDashBoardComponents();
			for (DashBoardComponents component : components) {
				DashBoardSummaryComponent summary = component.getSummary();
				if (summary.getEnv().equals("global") && summary.getType().equals("alertBoard") && summary.getTitle().equals("DashBoard"))
					summaries.add(summary);
			}
			for (DashBoardComponents component : components) {
				DashBoardSummaryComponent summary = component.getSummary();
				if (!(summary.getEnv().equals("global") && summary.getType().equals("alertBoard") && summary.getTitle().equals("DashBoard")))
					summaries.add(summary);
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
	

		initEnvItems();
	}
	
	public DashBoardComponents getComponents() {
		return components;
	}
		
	public void setItem(NavigationItemType itemType) {
		this.itemType = itemType;
		initEnvItems();
	}
	
	public NavigationItemType getItem() {
		return itemType;
	}
	
	public String getEnvironment() {
		return itemType.getEnv();
	}

	public String getType() {
		return itemType.getType();
	}	
	
	public String getTitle() {
		return itemType.getTitle();
	}

	public void setEnvironment(String env) {}
	public void setType(String type) {}
	public void setTitle(String title) {}
	
	
	public SelectItem[] getEnvironmentItems() {
		return environmentItems;
	}
	
	public SelectItem[] getTypeItems() {	
		return typeItems;
	}	
	
	public SelectItem[] getTitleItems() {	
		return titleItems;
	}		
	
	public void onChangeEnvironment(ValueChangeEvent evnt) {
		itemType.setEnv((String)evnt.getNewValue());	
		changeTypeItems();
	}	
	
	public void onChangeType(ValueChangeEvent evnt) {
		itemType.setType((String)evnt.getNewValue());	
		changeTitleItems();
	}
	
	public void onChangeTitle(ValueChangeEvent evnt) {
		itemType.setTitle((String)evnt.getNewValue());
		updatedSelectedDashBoard();
	}
	
	public Collection<DashBoardSummaryComponent> getSummaries() {
		return summaries;
	}
	
	private void initEnvItems() {	
		try {
			Collection<String> envs = new HashSet<String>();

			for (DashBoardSummaryComponent summary : summaries) {
				envs.add(summary.getEnv());
			}
			
			environmentItems = new SelectItem[envs.size()];
			
			boolean reset = true;
			
			int i=0;
			for(String env : envs) {		
				environmentItems[i++] = new SelectItem(env, env);
				if (env.equals(itemType.getEnv())) {
					reset = false;
				}
			}

			if (reset) {
				itemType.setEnv((String)environmentItems[0].getValue());
			}
			
			changeTypeItems();
		} catch (Exception exc) {
			logger.error(exc);
		}
	}	
	
	private void changeTypeItems() {		
		try {
			Collection<String> types = new HashSet<String>();

			for (DashBoardSummaryComponent summary : summaries) {
				if (summary.getEnv().equals(itemType.getEnv())) {
					types.add(summary.getType());
				}
			}
			
			typeItems = new SelectItem[types.size()];
			
			boolean reset = true;
			
			int i=0;
			for(String type : types) {		
				typeItems[i++] = new SelectItem(type, type);
				if (type.equals(itemType.getType())) {
					reset = false;
				}
			}

			if (reset) {
				itemType.setType((String)typeItems[0].getValue());
			}
			
			changeTitleItems();
		} catch (Exception exc) {
			logger.error(exc);
		}
	}	
	
	private void changeTitleItems() {
		try {
			Collection<String> titles = new HashSet<String>();

			for (DashBoardSummaryComponent summary : summaries) {
				if (summary.getEnv().equals(itemType.getEnv()) && 
					summary.getType().equals(itemType.getType())) {
					titles.add(summary.getTitle());
				}
			}
			
			titleItems = new SelectItem[titles.size()];
			
			boolean reset = true;
			
			int i=0;
			for(String title : titles) {		
				titleItems[i++] = new SelectItem(title, title);
				if (title.equals(itemType.getTitle())) {
					reset = false;
				}
			}

			if (reset) {
				itemType.setTitle((String)titleItems[0].getValue());
			}
			
			updatedSelectedDashBoard();
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	public void updatedSelectedDashBoard() {
		RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
		components = factory.getDashBoardComponents(getEnvironment(),
													getType(),
													getTitle());
	}
}
