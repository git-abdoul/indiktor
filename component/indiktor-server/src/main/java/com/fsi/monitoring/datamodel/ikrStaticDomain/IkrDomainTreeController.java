package com.fsi.monitoring.datamodel.ikrStaticDomain;

import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import com.fsi.monitoring.datamodel.tree.AbstractTreeController;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;

public class IkrDomainTreeController 
extends AbstractTreeController {

    public IkrDomainTreeController() {}
    
	public void initController(ActionEvent action) {
		if (!isAuthorized(43,"ikrDomainConfig")) {
			return;
		} 
    	try {	    	
	        // create root node with its children expanded
	        DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode();
	        
	        IkrStaticDomain rootDomain = new IkrStaticDomain(0);
	        IkrStaticDomainObject rootObject = new IkrStaticDomainObject(rootTreeNode, rootDomain);
	        
	        super.init(rootTreeNode);
    	} catch(Exception exc) {
    		System.out.println(exc);
    	}
    }
}
