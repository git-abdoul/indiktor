package com.fsi.monitoring.util;

import javax.faces.event.ActionEvent;

/**
 * Class used to allow the dynamic opening and closing of panelPopups
 * That means the visibility status is tracked, as well as supporting
 *  methods for button clicks on the page
 */
public class PopupBean {

	private boolean rendered = false;    
    
    public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public void closePopup(ActionEvent event) {
    	rendered = false;
    }
    
    public void openPopup(ActionEvent event) {
    	rendered = true;
    }
}
