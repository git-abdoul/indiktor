package com.fsi.monitoring.alert.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.bean.AlertModifierBean;
import com.fsi.monitoring.alert.config.AlertDefinitionSortableList;
import com.fsi.monitoring.alert.config.definition.AlertDefinitionBean;
import com.fsi.monitoring.alert.selection.AlertSelector;
import com.fsi.monitoring.alert.selection.AlertSelectorItemVisitor;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;


public class AlertManagerBean 
extends AlertDefinitionSortableList
implements AlertSelectorItemVisitor {
	
	private static final Logger logger = Logger.getLogger(AlertManagerBean.class);
	
	private String colors;
	
	private boolean upOn = true;
	private boolean downOn = false;
	private boolean ackOn = false;
	private boolean lowOn = true;
	private boolean mediumOn = true;
	private boolean highOn = true;
	private boolean notRunningOn = true;
	private boolean filterOn = false;

	private boolean selectAll = false;
	private boolean rendererAck;
	
	// dataTableColumn Names
	public enum ColumnName {State,Date,Acknowledged};

	private AlertModifierBean selectedAlertBean;
	
	private List<AlertModifierBean> alertBoard;
	private List<AlertModifierBean> alertToDisplay;
	
    private AlertSelector alertSelector;
    
    private SelectItem[] alertItems;
	
	public AlertManagerBean() {
		super(ColumnName.Date.name());
		comparator = new AlertBeanComparator();
		alertSelector = new AlertSelector();
	}
	
	public void init(ActionEvent action) {
		alertSelector.init(this);
		setAction("alertManager");
		selectAll = false;
	}
	
	public AlertSelector getAlertSelector() {
		return alertSelector;
	}
	
	private Collection<AlertModifierBean> sortDisplay() {
		List<AlertModifierBean> displayBeans = alertSelector.getDisplayedBeans();
		
		if (displayBeans.size() > 0) {
			Collections.sort(displayBeans, comparator);
			computeColors(displayBeans);
		}

		return displayBeans;
	}	
	
	public void displayBeansUpdated() {
		sortDisplay();
	}	
	
	public Collection<AlertModifierBean> getAlertBeans() {
		alertToDisplay = new ArrayList<AlertModifierBean>();
		alertSelector.launchUpdateReferenceBeans();
		alertBoard = alertSelector.getDisplayedBeans();
		
		for(AlertModifierBean alert : alertBoard) {
			String state = alert.getAlertBean().getAlertState().getSeverityName();
			if(upOn) {
				if(lowOn) {
					if(state.equals("LOW")) {
						alertToDisplay.add(alert);
						continue;
					}
				}
				if(mediumOn) {
					if(state.equals("MEDIUM")) {
						alertToDisplay.add(alert);
						continue;
					}
				}
				if(highOn) {
					if(state.equals("HIGH")) {
						alertToDisplay.add(alert);
						continue;
					}
				}
				if(notRunningOn) {
					if(state.equals("NOT RUNNING")) {
						alertToDisplay.add(alert);
						continue;
					}
				}
			}
			if(downOn) {
				if(state.equals("DOWN")) {
					alertToDisplay.add(alert);
					continue;
				}
			}
			if(ackOn) {
				if(state.equals("ACKNOWLEGDE")) {
					alertToDisplay.add(alert);
					continue;
				}
			}
		}
		
		computeColors(alertToDisplay);
		
		if(upOn && downOn && ackOn && lowOn && mediumOn && highOn && notRunningOn)
			filterOn = false;
		else
			filterOn = true;
		
		return alertToDisplay;
	}
	
	public boolean getAlertMessage() {
		if(alertToDisplay != null) {
			if(getAlertSize() == 0 && getAlertBeans().size() != 0)
				return false;
			else if(getAlertSize() != 0 && getAlertBeans().size() == 0)
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	public int getAlertSize() {
		int ret = 0;
		if (alertSelector.getDisplayedBeans() != null)
			ret = alertSelector.getDisplayedBeans().size();
		return ret;
	}
	
	public String getColors() {
		return colors;
	}	

	public void initBean() {}
			
	public synchronized void validate(ActionEvent event) {
		if (isAuthorized(23,"alertManager")) {
			
		}
	}
	
	public void alertSelectionChanged(ValueChangeEvent event) {
		AlertModifierBean checkedAlert = (AlertModifierBean)event.getComponent().getAttributes().get("checkedAlert");
		for (AlertModifierBean bean : alertBoard) {
			if (checkedAlert.getAlertBean().getAlertDefinitionId() == bean.getAlertBean().getAlertDefinitionId())
				bean.setChecked(1);
			else
				bean.setChecked(0);
		}
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return false;
	}
	
	@Override
	protected void sort() {
	}
	
	public long getSelectedAlertId() {
		long id = 0;
		if (selectedAlertBean != null)
			id = selectedAlertBean.getAlertBean().getAlert().getId();
		return id;
	}
	
	public AlertModifierBean getSelectedAlertBean() {
		return selectedAlertBean;
	}
	
	public void setSelectedAlertBean(AlertModifierBean selectedAlertBean) {
		this.selectedAlertBean = selectedAlertBean;
	}

	private void computeColors(Collection<AlertModifierBean> displayBeans) {
		//	System.out.println("Compute colors");
		StringBuffer colorBuffer = new StringBuffer();	
		for (AlertModifierBean alertModifierBean : displayBeans) {
			colorBuffer.append(alertModifierBean.getAlertBean().getColorStr());
			colorBuffer.append(",");
		}
		colors = colorBuffer.toString();
	}
	private void computeColorsFiltered(Collection<AlertModifierBean> displayBeans) {
		//	System.out.println("Compute colors");
		StringBuffer colorBuffer = new StringBuffer();	
		for (AlertModifierBean alertModifierBean : displayBeans) {
			colorBuffer.append(alertModifierBean.getAlertBean().getColorStr());
			colorBuffer.append(",");
		}
		colors = colorBuffer.toString();
	}
	
	public void handleUpOn(ValueChangeEvent evt) {
		upOn = !upOn;
		if(!upOn) {
			lowOn = false;
			mediumOn = false;
			highOn = false;
			notRunningOn = false;
		}
	}
	
	public void handleDownOn(ValueChangeEvent evt) {
		downOn = !downOn;
	}
	
	public void handleAckOn(ValueChangeEvent evt) {
		ackOn = !ackOn;
	}
	
	public void handleLowOn(ValueChangeEvent evt) {
		lowOn = !lowOn;
	}
	
	public void handleMediumOn(ValueChangeEvent evt) {
		mediumOn = !mediumOn;
	}
	
	public void handleHighOn(ValueChangeEvent evt) {
		highOn = !highOn;
	}
	
	public void handleNotRunningOn(ValueChangeEvent evt) {
		notRunningOn = !notRunningOn;
	}
	
	public boolean isUpOn() {
		return upOn;
	}

	public boolean isDownOn() {
		return downOn;
	}

	public boolean isAckOn() {
		return ackOn;
	}

	public boolean isLowOn() {
		return lowOn;
	}

	public boolean isMediumOn() {
		return mediumOn;
	}

	public boolean isHighOn() {
		return highOn;
	}

	public boolean isNotRunningOn() {
		return notRunningOn;
	}

	public boolean isFilterOn() {
		return filterOn;
	}

	public void setUpOn(boolean upOn) {
		this.upOn = upOn;
	}

	public void setDownOn(boolean downOn) {
		this.downOn = downOn;
	}

	public void setAckOn(boolean ackOn) {
		this.ackOn = ackOn;
	}

	public void setLowOn(boolean lowOn) {
		this.lowOn = lowOn;
	}

	public void setMediumOn(boolean mediumOn) {
		this.mediumOn = mediumOn;
	}

	public void setHighOn(boolean highOn) {
		this.highOn = highOn;
	}

	public void setNotRunningOn(boolean notRunningOn) {
		this.notRunningOn = notRunningOn;
	}
	
	public void handleSelectedAlert(ValueChangeEvent event) {
		AlertModifierBean checkedAlert = (AlertModifierBean)event.getComponent().getAttributes().get("checkedAlert");
		for(AlertModifierBean alert : alertToDisplay) {
			if(alert.equals(checkedAlert))
				alert.setSelected(true);
		}
	}
	
	public void handleSelectAllAlerts(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		for(AlertModifierBean alert : alertToDisplay) {
			alert.setSelected((Boolean)evt.getNewValue());
		}
	}

	public boolean isSelectAll() {
		return selectAll;
	}
	
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public void ackThisAlert(ActionEvent action) {		
		AlertModifierBean checkedAlert = (AlertModifierBean)action.getComponent().getAttributes().get("alert");
		if(!checkedAlert.getAlertBean().getAlert().isAcknowledged())
			checkedAlert.acknowledge(action);
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("This alert definition has already been acknowleged");
		}
	}
	
	public void acknowledgeSelectedAlerts(ActionEvent action) {
		int nAlertSelected = 0;
		int nAlertSelectedAck = 0;
		for(AlertModifierBean alert : alertToDisplay) {
			if(alert.isSelected()) {
				nAlertSelected++;
			}
		}
		if(nAlertSelected == 0) {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No alert definition has been selected");
		}
		else {
			for(AlertModifierBean alert : alertToDisplay) {
				if(alert.isSelected() && !alert.getAlertBean().getAlert().isAcknowledged()) {
					alert.acknowledge(action);
					nAlertSelectedAck++;
				}
			}
			if(nAlertSelectedAck == 0 && nAlertSelected == 1) {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(true);
				error.setModal(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("The alert definition selected has already been acknowleged");
			}
			else if(nAlertSelectedAck == 0 && nAlertSelected > 1) {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(true);
				error.setModal(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("The alert definitions selected have already been acknowleged");
			}
		}
	}
	
	public boolean isRendererAck() {
		rendererAck = false;
		if(alertToDisplay != null) {
			for(AlertModifierBean alert : alertToDisplay) {
				if(alert.isSelected()) {
					rendererAck = true;
					break;
				}
			}
		}
		return rendererAck;
	}

	private class AlertBeanComparator 
	implements Comparator<AlertModifierBean> {
	    
		private AlertDefinitionComparator adComparator = new AlertDefinitionComparator();
		
		public int compare(AlertModifierBean o1, AlertModifierBean o2) {
			ColumnName sortColumn = null;
			try {
				sortColumn = ColumnName.valueOf(sortColumnName);
		
		        if (sortColumn == ColumnName.State) {
		        	int severity1 = o1.getAlertBean().getAlert().getState().getSeverity();
		        	int severity2 = o2.getAlertBean().getAlert().getState().getSeverity();
		            return ascending ? severity1-severity2 : severity2-severity1;
		        } else if (sortColumn == ColumnName.Date) {
		        	Date d1 = o1.getAlertBean().getAlert().getAlertEvent().getEventDate();
		        	Date d2 = o2.getAlertBean().getAlert().getAlertEvent().getEventDate();
		        	return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
		        } else if (sortColumn == ColumnName.Acknowledged) {
		        	Boolean ack1 = o1.getAlertBean().getAlert().isAcknowledged();
		        	Boolean ack2 = o2.getAlertBean().getAlert().isAcknowledged();	
		        	return ascending ? ack1.compareTo(ack2) : ack2.compareTo(ack1);
		        } 
			} catch (Exception exc) {}
	        
			if (sortColumn == null) {
	        	AlertDefinitionBean ad1 = o1.getAlertBean().getAlertDefinitionBean();
	        	AlertDefinitionBean ad2 = o2.getAlertBean().getAlertDefinitionBean();
	        	return adComparator.compare(ad1, ad2);
	        }
			
			return 0;
	    }
	};	
}
