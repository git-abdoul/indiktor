package com.fsi.monitoring.datamodel.tree.userObject;

import java.util.Enumeration;

import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import com.fsi.monitoring.datamodel.selection.PanelStackBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.tree.IceUserObject;

public abstract class AbstractObject 
extends IceUserObject {	
	
	protected boolean loaded = false;
	protected PanelStackBean panelStack;
	protected String selectedObject;
	
	public AbstractObject(DefaultMutableTreeNode wrapper) {
		super(wrapper);
		wrapper.setUserObject(this);
		panelStack = (PanelStackBean)FacesUtils.getManagedBean("panelStack");
	}
    
	@Override
	public void setExpanded(boolean isExpanded) {
		super.setExpanded(isExpanded);
	}
	
	public void selectNodeObject(ActionEvent action) {
		panelStack.selectDefaultPanel();
	}
	
	public AbstractObject expandFromRoot(Object[] path) {
		int level = this.getWrapper().getLevel();
		if (level < path.length-1) {
			AbstractObject obj = (AbstractObject)path[level+1];
			String nodeIdToFound = obj.getId();
			setExpanded(true);
	        Enumeration nodes = this.getWrapper().depthFirstEnumeration();
	        while (nodes.hasMoreElements()) {
	        	DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
	        	AbstractObject tmp = (AbstractObject) node.getUserObject();
	            if (nodeIdToFound.equals(tmp.getId())) {
	                return tmp.expandFromRoot(path);
	            }
	        }
		}
		return this;
	}
	
	public String getId() {
		return this.getClass().getName() +  hashCode();
	}
}
