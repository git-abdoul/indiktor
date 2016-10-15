package com.fsi.monitoring.datamodel.ikrStaticDomain;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.tree.userObject.AbstractObject;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.FacesUtils;

public class IkrStaticDomainObject
extends AbstractObject {
	
	private IkrStaticDomain domain = null;
	
	public IkrStaticDomainObject(DefaultMutableTreeNode wrapper,
								 IkrStaticDomain domain) {
		super(wrapper);
		this.domain = domain;	
	}
	
	public String getId() {
		return domain.getDomainValue() + "-" + domain.getId();
	}	
	
	@Override
	public void setExpanded(boolean isExpanded) {		
		super.setExpanded(isExpanded);
		if (!loaded) {
			try {
				DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
				List<IkrStaticDomain> ikrStaticDomains = dataModelPM.getIkrStaticDomains(domain.getId());
				
				Collections.sort(ikrStaticDomains, new Comparator<IkrStaticDomain>() {
					public int compare(IkrStaticDomain o1, IkrStaticDomain o2) {
						return o1.getDomainValue().compareTo(o2.getDomainValue());
					}
				});
				
			    for (IkrStaticDomain ikrStaticDomain : ikrStaticDomains) {		        	
			        DefaultMutableTreeNode branchNode = new DefaultMutableTreeNode();
			        IkrStaticDomainObject branchObject = new IkrStaticDomainObject(branchNode,ikrStaticDomain);
			        
			        boolean isLeaf = wrapper.getLevel() == (IkrStaticDomainCreateBean.IKR_CATEGORY_LEVEL-1);
			        branchObject.setLeaf(isLeaf);
			        
			        wrapper.add(branchNode);
				}
			    loaded = true;
	    	} catch(Exception exc) {
	    		System.out.println(exc);
	    	}
		}
	}
	
	@Override
	public String getText() {
		return domain.getDomainValue();
	}
	
	@Override
	public void selectNodeObject(ActionEvent action) {
		IkrStaticDomainCreateBean staticDomainCreateBean = (IkrStaticDomainCreateBean)FacesUtils.getManagedBean("staticDomainBean");	
		staticDomainCreateBean.setDomain(domain, wrapper.getLevel());
	}
}
