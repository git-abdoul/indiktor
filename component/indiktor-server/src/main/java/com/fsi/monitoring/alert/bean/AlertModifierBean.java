package com.fsi.monitoring.alert.bean;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.swing.Action;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.Alert;
import com.fsi.monitoring.alert.AlertCompute;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;
import com.fsi.monitoring.alert.config.definition.AlertDefinitionBean;
import com.fsi.monitoring.alert.workflow.AlertCommentEvent;
import com.fsi.monitoring.alert.workflow.AlertComputeEvent;
import com.fsi.monitoring.alert.workflow.AlertWorkflow;
import com.fsi.monitoring.realTime.listener.AlertRealTimeListener;
import com.fsi.monitoring.sec.SecurityBean;
import com.fsi.monitoring.user.User;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;

public class AlertModifierBean extends AccessControlBean
implements Serializable {
	
	private static final long serialVersionUID = -4862200579737596290L;
	
	private static final Logger logger = Logger.getLogger(AlertModifierBean.class);	
	
	private boolean rendererAlertDetail = false;
	private boolean rendererAddComment = false;
	private boolean rendererAlertEvents = false;
	private boolean rendererNoComments = false;
	private boolean newCommentLine = false;
	private boolean newCommentLineEdit = false;
	
	private AlertBean alertBean;
	
	private String comment = null;
	private String commentInEdition = "";
	
	private AlertPM alertPM;
	
	private int checked;
	private boolean selected;
	private boolean selectedInDashboard;
	
	public boolean isAvailable() {
		return alertBean.getAlert() != null;
	}
	
	public void setAlertBean(AlertBean alertBean) {
		this.alertBean = alertBean;
	}

	public AlertBean getAlertBean() {
		return alertBean;
	}
	
	public AlertDefinitionBean getAlertDefinitionBean() {
		return alertBean.getAlertDefinitionBean();
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public String getName() {
		return alertBean.getAlertDefinitionBean().getAlertDefinition().getName();
	}
	
	public void acknowledge(ActionEvent action) {
		if (!isAuthorized(23,"")) {
			setAccessDenied();
			return;
		}
		
		if (comment!=null && comment.length()>0)
			comment = "ACK - " + comment;
		else
			comment = "ACK";
		addComment(true);
	}
	
	public void addComment(ActionEvent action) {
		if(comment.trim() != null && comment.trim().length() > 0)
			addComment(false);
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("You cannot add an empty comment !");
		}
	}	
	
	private void addComment(boolean acknowledge) {
		try {				
			AlertWorkflow oldState = alertBean.getAlert().getState();
			AlertWorkflow newState = acknowledge ? AlertWorkflow.ACK : oldState;
			
			SecurityBean securityBean = (SecurityBean)FacesUtils.getManagedBean("securityBean");
			AlertRealTimeListener listener = (AlertRealTimeListener)FacesUtils.getManagedBean("alertRTListener");
			User user = securityBean.getUser();
			
			long alertDefinitionId = alertBean.getAlertDefinitionBean().getAlertDefinition().getId();
			Alert alert = alertBean.getAlert();
			
			if (!acknowledge) {
				AlertCommentEvent commentEvent = new AlertCommentEvent(new Date(), oldState, oldState, comment,user.getId());
				try {
					alert.addEvent(commentEvent, true);
					long evtId = alertPM.addAlertEvent(alert, commentEvent);
					commentEvent.setId(evtId);
					listener.updateAlertBean(alertDefinitionId, alert, false, null);
				} catch (Exception exc) {
					logger.error("Error while saving alerts when adding comment", exc);
				}
			}
			else {				
				if (alert.getState() != AlertWorkflow.ACK) {
					AlertCommentEvent commentEvent = new AlertCommentEvent(new Date(), oldState, oldState, comment,user.getId());
					AlertComputeEvent computeEvent = new AlertComputeEvent(new Date(), oldState, newState);	
					
					// can't acknowledged an alert if the status has changed or if it is already ACK
					List<AlertCompute> computes = alertBean.getAlertDefinitionBean().getAlertDefinition().getAlertComputes();
					Map<AlertCompute,ComputeStatus> newComputeStatus = new TreeMap<AlertCompute,ComputeStatus>();
					for (AlertCompute alertCompute : computes) {
						newComputeStatus.put(alertCompute, ComputeStatus.DOWN);
					}				
					try {
						alert.addEvent(commentEvent, true);
						long evtId = alertPM.addAlertEvent(alert, commentEvent);
						commentEvent.setId(evtId);						
						listener.updateAlertBean(alertDefinitionId, alert, false, null);
						
						alert.setComputeStatus(newComputeStatus);
						alert.addEvent(computeEvent, true);
						evtId = alertPM.addAlertEvent(alert, computeEvent);
						computeEvent.setId(evtId);						
						listener.updateAlertBean(alertDefinitionId, alert, false, null);
					} catch (Exception exc) {
						logger.error("Error while saving alerts - acknowledge ...", exc);
					}
				}
			}
			
			comment = "";
		} catch(Exception exc){
				logger.error("Error when adding alert comment",exc);			
		}
		rendererAddComment = false;
		rendererNoComments = false;
		newCommentLine = false;
	}
	
	public void deleteComment(ActionEvent action) {
		Collection<AlertCommentBean> commentBeans = alertBean.getAlertCommentBeans();
		AlertCommentEvent eventToDelete = (AlertCommentEvent)action.getComponent().getAttributes().get("eventToDelete");
		for(AlertCommentBean commentBean : commentBeans) {
			if(commentBean.getEvent().getId() == eventToDelete.getId()) {
				try {
					AlertRealTimeListener listener = (AlertRealTimeListener)FacesUtils.getManagedBean("alertRTListener");
					
					long alertDefinitionId = alertBean.getAlertDefinitionBean().getAlertDefinition().getId();
					Alert alert = alertBean.getAlert();
					try {
						alert.deleteEvent(eventToDelete);
						alertPM.removeAlertEvent(alert, eventToDelete);
						listener.updateAlertBean(alertDefinitionId, alert, true, eventToDelete);
					} catch (Exception exc) {
						logger.error("Error while saving alerts when deleting comment", exc);
					}			
					comment = "";
				} catch(Exception exc){
						logger.error("Error when deleting alert comment",exc);			
				}
			}
		}
	}
	
	public void editComment(ActionEvent action) {
		AlertCommentEvent eventToEdit = (AlertCommentEvent)action.getComponent().getAttributes().get("eventToEdit");
		commentInEdition = eventToEdit.getComment();
		newCommentLineEdit = true;
	}
	
	public void validateEditComment(ActionEvent action) {	
		if(commentInEdition.trim() != null && commentInEdition.trim().length() > 0) {
			newCommentLineEdit = false;
			comment = commentInEdition;
			Collection<AlertCommentBean> commentBeans = alertBean.getAlertCommentBeans();
			AlertCommentEvent eventToEdit = (AlertCommentEvent)action.getComponent().getAttributes().get("eventToEdit");
			for(AlertCommentBean commentBean : commentBeans) {
				if(commentBean.getEvent().getId() == eventToEdit.getId()) {
					try {
						SecurityBean securityBean = (SecurityBean)FacesUtils.getManagedBean("securityBean");
						User user = securityBean.getUser();
	
						AlertRealTimeListener listener = (AlertRealTimeListener)FacesUtils.getManagedBean("alertRTListener");
											
						long alertDefinitionId = alertBean.getAlertDefinitionBean().getAlertDefinition().getId();
						Alert alert = alertBean.getAlert();
						AlertCommentEvent commentEvent = new AlertCommentEvent(new Date(), eventToEdit.getOldState(), eventToEdit.getOldState(), comment,user.getId());
						commentEvent.setId(eventToEdit.getId());
						try {
							alert.deleteEvent(eventToEdit);
							alert.addEvent(commentEvent, true);
							alertPM.modifyAlertEvent(alert, commentEvent);
							listener.updateAlertBean(alertDefinitionId, alert, true, eventToEdit);
							listener.updateAlertBean(alertDefinitionId, alert, false, commentEvent);
						} catch (Exception exc) {
							logger.error("Error while saving alerts when deleting comment", exc);
						}			
						comment = "";
					} catch(Exception exc){
							logger.error("Error when deleting alert comment",exc);			
					}
				}
			}
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("You cannot add an empty comment !");
		}
	}
	
	public void cancelEditComment(ActionEvent action) {
		commentInEdition = null;
		newCommentLineEdit = false;
	}

	public void setChecked(int checked) {
		this.checked = checked;
	}

	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

//	public void setSelected(boolean selected) {
//		if (selected && checked==1)
//			this.selected = true;
//		else {
//			this.selected = false;
//		}
//	}	

	public boolean isSelectedInDashboard() {
		return selectedInDashboard;
	}

	public void setSelectedInDashboard(boolean selectedInDashboard) {
		this.selectedInDashboard = selectedInDashboard;
	}

	public boolean isRendererAddComment() {
		return rendererAddComment;
	}
	
	public void openAddCommentPopup(ActionEvent event) {
		rendererAddComment = true;
	}
	
	public void closeAddCommentPopup(ActionEvent event) {
		rendererAddComment = false;
	}
	
	public boolean isRendererAlertDetail() {
		return rendererAlertDetail;
	}
	
	public void openAlertDetailPopup(ActionEvent event) {
		rendererAlertDetail = true;
	}
	
	public void closeAlertDetailPopup(ActionEvent event) {
		rendererAlertDetail = false;
	}
	
	public void onChangeSelected(ValueChangeEvent evnt) {	
		this.selected = (Boolean)evnt.getNewValue();
	}

	public boolean isRendererAlertEvents() {
		Collection<AlertComputeEventBean> alertEmpty = getAlertBean().getAlertEventBeans();
		if (alertEmpty == null)
			rendererAlertEvents = true;
		else
			rendererAlertEvents = false;
		return rendererAlertEvents;
	}

	public boolean isRendererNoComments() {
		Collection<AlertCommentBean> commentEmpty = getAlertBean().getAlertCommentBeans();
		if (commentEmpty == null)
			rendererNoComments = true;
		else if (commentEmpty.isEmpty())
			rendererNoComments = true;
		else
			rendererNoComments = false;
		return rendererNoComments;
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}

	public void setAlertPM(AlertPM alertPM) {
		this.alertPM = alertPM;
	}	
	
	public void addNewCommentLine(ActionEvent event) {
		comment = null;
		newCommentLine = true;
	}
	
	public void cancelNewCommentLine(ActionEvent event) {
		comment = null;
		newCommentLine = false;
	}

	public boolean isNewCommentLine() {
		return newCommentLine;
	}
	
	public String getCommentInEdition() {
		return commentInEdition;
	}

	public void setCommentInEdition(String commentInEdition) {
		this.commentInEdition = commentInEdition;
	}

	public boolean isNewCommentLineEdit() {
		return newCommentLineEdit;
	}

	public String getScrollHeightForAlertBoard() {
		if(newCommentLine || newCommentLineEdit)
			return "193px";
		else
			return "235px";
	}
	
	public String getScrollHeightForDashBoard() {
		if(newCommentLine || newCommentLineEdit)
			return "158px";
		else
			return "200px";
	}
}
