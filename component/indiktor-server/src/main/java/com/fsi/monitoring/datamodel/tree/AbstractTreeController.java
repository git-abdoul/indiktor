package com.fsi.monitoring.datamodel.tree;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.fsi.monitoring.datamodel.tree.userObject.AbstractObject;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.tree.IceUserObject;

public abstract class AbstractTreeController
extends AccessControlBean {
	
    protected DefaultTreeModel model;
    protected AbstractObject selectedObject;
    
    public void init(DefaultMutableTreeNode rootTreeNode) {
        // model is accessed by by the ice:tree component
        model =  new DefaultTreeModel(rootTreeNode);         
        selectAndExpandDefaultNode();
    }
    
    public DefaultTreeModel getModel() {
    	return model;
    }
    
    public AbstractObject getSelectedObject() {
        return selectedObject;
    }

    public void nodeSelected(ActionEvent action) {
        String objectCode = FacesUtils.getRequestParameter("nodeObjectCode");
        selectedObject = findTreeNodeObject(objectCode);        
        selectedObject.selectNodeObject(action);
    }     
	
    public ArrayList<String> getSelectedTreePath() {
        Object[] objectPath = selectedObject.getWrapper().getUserObjectPath();
        ArrayList<String> treePath = new ArrayList<String>();
        Object anObjectPath;
        for(int i= 1, max = objectPath.length; i < max; i++){
            anObjectPath = objectPath[i];
            IceUserObject userObject = (IceUserObject) anObjectPath;
            treePath.add(userObject.getText());
        }
        return treePath;
    }
    
    private void selectAndExpandDefaultNode() {
    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
    	if (selectedObject == null) {
    		// root case
    		selectedObject = (AbstractObject)rootNode.getUserObject();
    	} else {
    		Object[] path = selectedObject.getWrapper().getUserObjectPath();
			
    		AbstractObject rootObject = (AbstractObject)rootNode.getUserObject();    		
    		selectedObject = rootObject.expandFromRoot(path);
    	}
		selectedObject.selectNodeObject(null);
		selectedObject.setExpanded(true);
    }

    private AbstractObject findTreeNodeObject(String nodeId) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode node;
        AbstractObject tmp;
        Enumeration nodes = rootNode.depthFirstEnumeration();
        while (nodes.hasMoreElements()) {
            node = (DefaultMutableTreeNode) nodes.nextElement();
            tmp = (AbstractObject) node.getUserObject();
            if (nodeId.equals(tmp.getId())) {
                return tmp;
            }
        }
        return null;
    }    

}
