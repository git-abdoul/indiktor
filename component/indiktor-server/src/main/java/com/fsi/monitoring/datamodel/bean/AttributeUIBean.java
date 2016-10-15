package com.fsi.monitoring.datamodel.bean;

import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;

public class AttributeUIBean {
	private UIOutput labelUI = null;
	private UIInput inputUI = null;
	
	public AttributeUIBean(UIOutput labelUI, UIInput inputUI) {
		super();
		this.labelUI = labelUI;
		this.inputUI = inputUI;
	}

	public UIOutput getLabelUI() {
		return labelUI;
	}

	public UIInput getInputUI() {
		return inputUI;
	}

	public void setInputUI(UIInput inputUI) {
		this.inputUI = inputUI;
	}	
}
