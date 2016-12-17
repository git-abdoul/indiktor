package com.fsi.monitoring.datamodel.selection;

import javax.faces.event.ActionEvent;


/**
 * <p>
 * The PanelStackBean is responsible for storing the name of the panel in the
 * panelStack which should be displayed when rendered. 
 * </p>
 * <p>
 * If a selectedPanel name is not found in the panel stack the panelStack
 * component will not change the current selected PanelStack
 * </p>
 */
public class PanelStackBean {

	private static final String DEFAULT_PANEL = "EMPTY";
	
    private String selectedPanel = DEFAULT_PANEL;
    
    public String getSelectedPanel() {
       return selectedPanel;
    }

    /**
     * 
     * @param selectedPanel
     */
    public void setSelectedPanel(String selectedPanel) {
    	this.selectedPanel = selectedPanel;
    }
 
    public void selectDefaultPanel() {
    	selectedPanel = DEFAULT_PANEL;
    }
    
	public void addMonitor(ActionEvent event) {
		System.out.println("----agent1Bean---");
//		PanelStackBean panelStack = (PanelStackBean)FacesUtils.getManagedBean("panelStack");
		setSelectedPanel("CACHE_MONITOR");
	}    
}
