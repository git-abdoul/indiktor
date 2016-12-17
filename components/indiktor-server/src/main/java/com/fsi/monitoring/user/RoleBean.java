package com.fsi.monitoring.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class RoleBean 
extends SortableList {
	
	private static final String nameColumnName = "Name";
	private static final String descriptionColumnName = "Description";
	
	private Role role;
	
	private String roleNameOld = "";
	private String descriptionOld = "";
	
	private boolean nameMandatory = false;
	private boolean nameError = false;
	private boolean noAccessPerm = false;
	private String nameStyle = "width:280px;";
	private String accessPermStyle = "border: 1px solid #336699; text-align: center; width: 100%;";
	
	private List<RoleItem> roleItems = null;
	private List<RoleItem> roleItemsSelected = null;
	
	public RoleBean () {super(nameColumnName);}
	
//	private RoleItem selectedRoleItem;
	
	private boolean rendererCreateNewRole = false;	
	private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;
	
	private String panelStack;
	
	private List<AccessPermBean> availableAccessPermBeans;
	private List<AccessPermBean> roleAccessPermBeans;
	
	int numberRolesSelected = 0;
	
	public Role getRole() {
		return role;
	}
	
	public List<AccessPermBean> getAvailableAccessPermBeans() {
		if (availableAccessPermBeans != null && availableAccessPermBeans.size() > 0) {
			Collections.sort(availableAccessPermBeans, new Comparator<AccessPermBean>() {
				public int compare(AccessPermBean o1, AccessPermBean o2) {
					return o1.getAccessPerm().getName().compareToIgnoreCase(o2.getAccessPerm().getName());
				}			
			});
		}		
		return availableAccessPermBeans;
	}
	
	public List<AccessPermBean> getRoleAccessPermBeans() {
		if (roleAccessPermBeans != null && roleAccessPermBeans.size() > 0) {
			Collections.sort(roleAccessPermBeans, new Comparator<AccessPermBean>() {
				public int compare(AccessPermBean o1, AccessPermBean o2) {
					return o1.getAccessPerm().getName().compareToIgnoreCase(o2.getAccessPerm().getName());
				}			
			});
		}		
		return roleAccessPermBeans;
	}
	
	public void initRole() {
		panelStack = "ROLE_ACCESS_PERM";
		availableAccessPermBeans = new ArrayList<AccessPermBean>();
		roleAccessPermBeans = new ArrayList<AccessPermBean>();
		
		List<Long> roleAccessPermIds = role.getAccessPermIds();	
		Map<Long,AccessPerm> accessPerms = getAccessPerms();		
		for (AccessPerm accessPerm : accessPerms.values()) {
			AccessPermBean accessPermBean = new AccessPermBean(accessPerm);
			if (roleAccessPermIds.contains(accessPerm.getId())) {
				roleAccessPermBeans.add(accessPermBean);
			} else {
				availableAccessPermBeans.add(accessPermBean);
			}
		}
	}
	
	public String getDeleteMessage() {
		numberRolesSelected = 0;
		for (RoleItem roleItem : roleItems) {
			if (roleItem.getSelected()){
				numberRolesSelected++;
			}
		}
		String message = "No components selected";
		if (numberRolesSelected == 1) {
			for (RoleItem roleItem : roleItems) {
				if(roleItem.getSelected()) {
					message = "Are you sure to delete this role : " + roleItem.getRole().getName();
					break;
				}
			}
			return message;
		}
		else {
			message = "Are you sure to delete these " + numberRolesSelected + " roles?";
			return message;
		}
	}
	
//	private void resetRole() {
//		role = new Role();
//		
//		accessPermBeans = new ArrayList<AccessPermBean>();
//		
//		Map<Long,AccessPerm> accessPerms = getAccessPerm();
//		
//		for (AccessPerm accessPerm : accessPerms.values()) {
//			AccessPermBean accessPermBean = new AccessPermBean();
//			accessPermBean.setAccessPerm(accessPerm);
//			accessPermBeans.add(accessPermBean);
//		}
//	}
	
//	public void initNewRole() {
//		resetRole();
//	}
	
	public void init(ActionEvent action) {
		if (!isAuthorized(121,"roles")) {
			return;
		}	
		roleItemsSelected = new ArrayList<RoleItem>();
		selectAll = false;
		updateRoles();		
	}
	
	public void pageChangeListener(ActionEvent action) {
		init(null);
	}
	
	private void updateRoles() {
		roleItems = new ArrayList<RoleItem>();		
		UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
		try {
			Map<Long, Role> roles = userPM.getRoles();
			
			for(Role role : roles.values()) {
				roleItems.add(new RoleItem(role));
			}
		} catch(Exception exc) {
			System.out.println(exc);
		}
	}
		
	
	public List<RoleItem> getRoleItems() {
		if (roleItems != null && roleItems.size()>0)
			sort();
		return roleItems;
	}
	
	public void validate(ActionEvent event) {
//		if (!isAuthorized(123,"")) {
//			return;
//		}			
//		if(role.getName()==null && role.getName().length()==0) {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.init();
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Role Name can't be empty. Please set a Role Name !!!");
//			return;
//		}

		testFields();
		if (!nameMandatory && !nameError && !noAccessPerm) {			
			UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());		
			List<Long> selectedAccessPermIds = new ArrayList<Long>();
			
			for(AccessPermBean accessPermBean : roleAccessPermBeans) {
				selectedAccessPermIds.add(accessPermBean.getAccessPerm().getId());
			}
			
			role.setAccessPermIds(selectedAccessPermIds);
			
			try {
				userPM.updateRole(role);
				
			} catch(Exception exc) {
				System.out.println(exc);
			}		
			
			updateRoles();
			rendererCreateNewRole = false;
			selectAll = false;
			roleItemsSelected = new ArrayList<RoleItem>();
		}	
		else {
			return;
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.init();
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("You have to select at least one Access Perm !!!");
		}		
	}
	
	public void testFields() {
		if(role.getName().trim() == null || role.getName().trim().length() == 0) {
			nameMandatory = true;
			nameStyle = "width:280px; border: 1px red solid;";
		}
		else {
			nameMandatory = false;
			nameStyle = "width:280px;";
		}
		
		if(!nameMandatory) {
			nameError = false;
			for(RoleItem roleitem : roleItems) {
				if(roleitem.getRole().getName().equalsIgnoreCase(role.getName())
						&& roleitem.getRole().getId() != role.getId()) {
					nameError = true;
					nameStyle = "width:280px; border: 1px red solid;";
				}
			}
		}
		
		if (roleAccessPermBeans.size()>0) {
			noAccessPerm = false;
			accessPermStyle = "border: 1px solid #336699; text-align: center; width: 100%;";
		}
		else {
			noAccessPerm = true;
			accessPermStyle = "border:1px solid red; text-align: center; width: 100%;";
		}
	}
	
	public void deleteRole(ActionEvent event) {
		if (!isAuthorized(122,"")) {
			setAccessDenied();
			return ;
		}
		RoleItem roleItemSelected = (RoleItem)event.getComponent().getAttributes().get("RoleItem");
		Role roleToTest = roleItemSelected.getRole();
		UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());		
		try {
			userPM.deleteRole(roleToTest.getId());
		} catch(Exception exc) {
			System.out.println(exc);
		}
		selectAll = false;
		roleItemsSelected = new ArrayList<RoleItem>();
		updateRoles();
		role = null;
	}
	
	public void deleteSelectedRoles(ActionEvent event) {
		if (!isAuthorized(122,"")) {
			setAccessDenied();
			return ;
		}	
		
		numberRolesSelected = 0;
		for (RoleItem roleItem : roleItems) {
			if (roleItem.getSelected()){
				numberRolesSelected++;
			}
		}
		if (numberRolesSelected > 0) {
			for (RoleItem roleItem : roleItems) {
				if (roleItem.getSelected()){
					Role roleToTest = roleItem.getRole();
					UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());		
					try {
						userPM.deleteRole(roleToTest.getId());
					} catch(Exception exc) {
						System.out.println(exc);
					}
				}
			}
			roleItemsSelected = new ArrayList<RoleItem>();
			selectAll = false;
			updateRoles();
			role = null;
		}	
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No role has been selected !!!");
		}
	}
	
	public void handleAccessPerm(ActionEvent event) {		
		if(role.getName()!=null && role.getName().length()>0) {
			panelStack = "MODIFY_ACCESS_PERM";
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("Role Name can't be empty. Please set a Role Name !!!");
		}
	}
	
	public void backToRoleDesc(ActionEvent event) {
		panelStack = "ROLE_ACCESS_PERM";
	}
	
	private Map<Long,AccessPerm> getAccessPerms() {
		Map<Long,AccessPerm> res = null;		
		UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
		try {
			res = userPM.getAccessPerms();
		} catch(Exception exc) {
			System.out.println(exc);
			res = new HashMap<Long, AccessPerm>();
		}
		
		return res;
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		RoleItem selectedRoleItem = roleItems.get(rowId);
		role = selectedRoleItem.getRole();
	}

//	public RoleItem getSelectedRoleItem() {
//		return selectedRoleItem;
//	}
//
//	public void setSelectedRoleItem(RoleItem selectedRoleItem) {
//		this.selectedRoleItem = selectedRoleItem;
//	}

	public boolean isRendererCreateNewRole() {
		return rendererCreateNewRole;
	}	
	
	public void openCreateNewRolePopup(ActionEvent event) {
		if (!isAuthorized(124, "")) {
			setAccessDenied();
			return;
		}
		rendererCreateNewRole = true;
		role = new Role();
		initRole();
	} 
	
	public void openUpdateRolePopup(ActionEvent event) {
		if (!isAuthorized(123, "")) {
			setAccessDenied();
			return;
		}
		RoleItem roleItemSelected = (RoleItem)event.getComponent().getAttributes().get("RoleItem");
		role = roleItemSelected.getRole();
		rendererCreateNewRole = true;
		initRole();
		roleNameOld = role.getName();
		descriptionOld = role.getDescription();
		
		
//		numberRolesSelected = 0;
//		for (RoleItem roleItem : roleItems) {
//			if (roleItem.getSelected()){
//				numberRolesSelected++;
//			}
//		}
//		
//		if (numberRolesSelected < 2)
//			if (numberRolesSelected == 1) {
//				rendererCreateNewRole = true;
//				initRole();
//			}	
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No role has been selected !!!");
//			}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one role to edit");
//		}
	} 
	
	public void closeCreateNewRolePopup (ActionEvent event) {
		rendererCreateNewRole = false;
		role.setName(roleNameOld);
		role.setDescription(descriptionOld);
		updateRoles();
		role = null;
		for(RoleItem roleItem : roleItems) {
			for(RoleItem roleItemSelected : roleItemsSelected) {
				if(roleItem.getRole().getName().equals(roleItemSelected.getRole().getName())
						&& roleItem.getRole().getDescription().equals(roleItemSelected.getRole().getDescription())) {
					roleItem.setSelected(true);
				}
			}
		}
		noAccessPerm = false;
		accessPermStyle = "border: 1px solid #336699; text-align: center; width: 100%;";
		nameError = false;
		nameMandatory = false;
		nameStyle = "width:280px;";
	}

	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}

	public boolean isPaginationVisible() {
		if (roleItems.size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}
	
	public void addAccessPermItem(ActionEvent event) {
		AccessPermBean accessPermBean = (AccessPermBean)event.getComponent().getAttributes().get("accessPermBean");
		roleAccessPermBeans.add(accessPermBean);
		availableAccessPermBeans.remove(accessPermBean);
		
//		List<AccessPermBean> tempList = new ArrayList<AccessPermBean>();
//		tempList.addAll(availableAccessPermBeans);
//		synchronized (availableAccessPermBeans) {
//			for (Iterator<AccessPermBean> it = availableAccessPermBeans.iterator(); it.hasNext();) {  
//				AccessPermBean item = it.next();  
//				if (item.getSelected()) {
//					item.setSelected(false);
//					roleAccessPermBeans.add(item);
//					tempList.remove(item);
//				}			
//			}
//		}
//		availableAccessPermBeans = tempList;
	}
	
	public void addAllAccessPermItem(ActionEvent event) {
		synchronized (availableAccessPermBeans) {
			for (Iterator<AccessPermBean> it = availableAccessPermBeans.iterator(); it.hasNext();) {  
				AccessPermBean item = it.next();  
				roleAccessPermBeans.add(item);	
			}
		}
		availableAccessPermBeans = new ArrayList<AccessPermBean>();
	}

	public void removeAccessPermItem(ActionEvent event) {
		AccessPermBean accessPermBean = (AccessPermBean)event.getComponent().getAttributes().get("accessPermBean");
		availableAccessPermBeans.add(accessPermBean);
		roleAccessPermBeans.remove(accessPermBean);
		
//		List<AccessPermBean> tempList = new ArrayList<AccessPermBean>();
//		tempList.addAll(roleAccessPermBeans);
//		synchronized (roleAccessPermBeans) {
//			for (Iterator<AccessPermBean> it = roleAccessPermBeans.iterator(); it.hasNext();) {  
//				AccessPermBean item = it.next();  
//				if (item.getSelected()) {
//					item.setSelected(false);
//					availableAccessPermBeans.add(item);
//					tempList.remove(item);
//				}			
//			}
//		}
//		roleAccessPermBeans = tempList;
	}
	
	public void removeAllAccessPermItem(ActionEvent event) {
		synchronized (roleAccessPermBeans) {
			for (Iterator<AccessPermBean> it = roleAccessPermBeans.iterator(); it.hasNext();) {  
				AccessPermBean item = it.next();  
				availableAccessPermBeans.add(item);
			}
		}
		roleAccessPermBeans = new ArrayList<AccessPermBean>();
	}
	
	public String getPanelStack() {
		return panelStack;
	}
	
	public boolean isRoleAccessPermEmpty() {
		return roleAccessPermBeans.isEmpty();
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}
	
	public String getNameColumnName() {
		return nameColumnName;
	}

	public String getDescriptionColumnName() {
		return descriptionColumnName;
	}
	
	public void handleSelectedRole(ValueChangeEvent event) {
		RoleItem roleItemSelected = (RoleItem)event.getComponent().getAttributes().get("RoleItem");
		if(roleItemSelected != null) {
			for(RoleItem roleItem : roleItems) {
				if(roleItem.equals(roleItemSelected)) {
					roleItem.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						roleItemsSelected.add(roleItemSelected);
					else
						roleItemsSelected.remove(roleItemSelected);
				}
			}
		}
	}
	
	public void handleSelectAllRoles(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		roleItemsSelected.clear();
		for(RoleItem roleItem : roleItems) {
			roleItem.setSelected((Boolean)evt.getNewValue());
			if((Boolean)evt.getNewValue())
				roleItemsSelected.add(roleItem);
		}
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public int getRoleItemsSelected() {
		int size = roleItemsSelected.size();
		return size;
	}

	public void setRoleItemsSelected(List<RoleItem> roleItemsSelected) {
		this.roleItemsSelected = roleItemsSelected;
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	@Override
	protected void sort() {
		Collections.sort(roleItems, new Comparator<RoleItem>() {
			public int compare(RoleItem o1, RoleItem o2) {
				int res = 0;
				try {
					if (getNameColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getRole().getName().toLowerCase().compareTo(o2.getRole().getName().toLowerCase()) :  o2.getRole().getName().toLowerCase().compareTo(o1.getRole().getName().toLowerCase());
					}
					if (getDescriptionColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getRole().getDescription().toLowerCase().compareTo(o2.getRole().getDescription().toLowerCase()) :  o2.getRole().getDescription().toLowerCase().compareTo(o1.getRole().getDescription().toLowerCase());
					}
				}
				catch (Exception e) {}
				return res;
			}
		});				
		
	}
	
	//-------------Control and style---------------//

	public boolean isNameMandatory() {
		return nameMandatory;
	}

	public boolean isNameError() {
		return nameError;
	}

	public boolean isNoAccessPerm() {
		return noAccessPerm;
	}

	public String getNameStyle() {
		return nameStyle;
	}

	public String getAccessPermStyle() {
		return accessPermStyle;
	}
	
	public boolean getListRendered() {
		return getRoleItems().size() > 0;
	}
}
