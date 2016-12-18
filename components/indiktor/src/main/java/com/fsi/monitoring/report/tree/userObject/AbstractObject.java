package com.fsi.monitoring.report.tree.userObject;

import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import com.icesoft.faces.component.tree.IceUserObject;

public abstract class AbstractObject 
extends IceUserObject {	
	
	protected boolean loaded = false;
	
	public AbstractObject(DefaultMutableTreeNode wrapper) {
		super(wrapper);
	}
    
	@Override
	public void setExpanded(boolean isExpanded) {
		super.setExpanded(isExpanded);
	}
	
//	public void selectNodeObject(ActionEvent action) {
//		panelStack.selectDefaultPanel();
//	}
	
	@Override
	public abstract String getText();
	
	public void selectNodeObject(ActionEvent action) {}	
}
