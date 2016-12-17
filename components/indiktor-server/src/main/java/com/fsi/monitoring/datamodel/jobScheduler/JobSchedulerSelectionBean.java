package com.fsi.monitoring.datamodel.jobScheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionBean;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionVisitor;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerStaticDomain;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.IkrUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class JobSchedulerSelectionBean
extends SortableList
implements LogicalEnvSelectionVisitor, Serializable {

	private static final long serialVersionUID = -3211187085936131426L;
	private static final Logger logger = Logger.getLogger(JobSchedulerSelectionBean.class);
	
	private static final String envColumnName = "Environment";
	private static final String jobTypeColumnName = "Job Type";
	private static final String jobNameColumnName = "Job Name";
	private static final String modeColumnName = "Mode";
	
	private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;
	
	private boolean rendererFilter = false;
	
	private JobSchedulerCreationBean jobSchedulerCreationBean;
	
	private IkrJobSchedulerConfig selectedJobSchedulerConfig;
	
	private List<JobSchedulerConfigBean> jobSchedulerConfigs;
	private List<JobSchedulerConfigBean> jobSchedulerConfigsSelected;
	private LogicalEnvSelectionBean logicalEnvSelectionBean;	
	
	private String searchQuery = "";
	
	public JobSchedulerSelectionBean() {
		super(jobNameColumnName);		
		logicalEnvSelectionBean = new LogicalEnvSelectionBean(false);
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}

	public void init(ActionEvent action) {
		if (!isAuthorized(105,"jobSchedulerSelection")) {
			return;
		}
		
		jobSchedulerCreationBean = (JobSchedulerCreationBean)FacesUtils.getManagedBean("jobSchedulerCreationBean");
		logicalEnvSelectionBean.accept(this);
		logicalEnvSelectionBean.init();
		if(!jobSchedulerCreationBean.isEdit()) {
			jobSchedulerConfigsSelected = new ArrayList<JobSchedulerConfigBean>();
			selectAll = false;
		}
		else {
			for(JobSchedulerConfigBean jobSchedulerConfig : jobSchedulerConfigs) {
				for(JobSchedulerConfigBean jobSchedulerConfigSelected : jobSchedulerConfigsSelected) {
					if(jobSchedulerConfig.getLogicalEnv().getName().equals(jobSchedulerConfigSelected.getLogicalEnv().getName())
							&& jobSchedulerConfig.getJobType().equals(jobSchedulerConfigSelected.getJobType())
								&& jobSchedulerConfig.getName().equals(jobSchedulerConfigSelected.getName())) {
						jobSchedulerConfig.setSelected(true);
					}
				}
			}
		}
	}
	
	public void pageChangeListener(ActionEvent action) {
		for(JobSchedulerConfigBean jobSchedulerConfig : jobSchedulerConfigs) {
			if(jobSchedulerConfig.isSelected()) {
				jobSchedulerConfig.setSelected(false);
			}
		}
		selectedJobSchedulerConfig = null;
		jobSchedulerConfigsSelected = new ArrayList<JobSchedulerConfigBean>();
		selectAll = false;
	}
	
	public List<JobSchedulerConfigBean> getJobSchedulerConfigs() {
		filterConfigs();
		if (jobSchedulerConfigs != null && jobSchedulerConfigs.size()>0)
			sort();
		return jobSchedulerConfigs;
	}
	
	public void searchMonitorQuery(ValueChangeEvent event) {
		searchQuery = (String)event.getNewValue();	
		reloadJobSchedulerList();
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		selectAll = false;
	}
	
	public void filterConfigs() {
		if (searchQuery!=null && searchQuery.length()>0) {
			searchQuery = searchQuery.toLowerCase();
			List<String> queryItems = IkrUtils.splitSearchIndex(searchQuery);
			String[] searchQueryItems = (String[])queryItems.toArray(new String[queryItems.size()]);
			List<JobSchedulerConfigBean> newJobSchedulerConfigList = new ArrayList<JobSchedulerConfigBean>();
			for (JobSchedulerConfigBean bean : jobSchedulerConfigs) {
				Set<String> indexes = bean.getSearchIndexes();
				String[] searchIndexes = (String[])indexes.toArray(new String[indexes.size()]);	
				if (IkrUtils.accepts(searchIndexes, searchQueryItems, true,  true))
					newJobSchedulerConfigList.add(bean);
			}
			jobSchedulerConfigs = new ArrayList<JobSchedulerConfigBean>();
			jobSchedulerConfigs.addAll(newJobSchedulerConfigList);
		}
	}

	public void setJobSchedulerConfigs(List<JobSchedulerConfigBean> jobSchedulerConfigs) {
		this.jobSchedulerConfigs = jobSchedulerConfigs;
	}
	
	public void changeLogicalEnv(int newLogicalEnv) {
		reloadJobSchedulerList();
	}

	public void reloadJobSchedulerList() {
		jobSchedulerConfigs = new ArrayList<JobSchedulerConfigBean>();
		try {
			logicalEnvSelectionBean.accept(this);
			
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			
			int logicalEnvId = 0;
			if (logicalEnvSelectionBean.getLogicalEnv()!=null)
				logicalEnvId = logicalEnvSelectionBean.getLogicalEnv().getId();
			
			Map<Integer,IkrJobSchedulerConfig> jobSchedulerConfigMap = dataModelPM.getJobSchedulerConfigs(logicalEnvId);
			
			for (IkrJobSchedulerConfig config : jobSchedulerConfigMap.values()) {
				IkrJobSchedulerStaticDomain domain = dataModelPM.getJobSchedulerStaticDomain(config.getJobStaticDomainId());
				LogicalEnv logicalEnv = logicalEnvSelectionBean.getLogicalEnv(config.getLogicalEnvId());
				jobSchedulerConfigs.add(new JobSchedulerConfigBean(logicalEnv,config, domain));
			}
    	} catch(Exception exc) {    		
    		logger.error(exc.getMessage(), exc);
    	}
	}
	
	public void createNewJobScheduler(ActionEvent event) {
		if (!isAuthorized(109,"")) {
			setAccessDenied();
			return;
		}
		jobSchedulerCreationBean.create();
	}
	
	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}

	public boolean isPaginationVisible() {
		if (jobSchedulerConfigs.size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}

	public void setPaginationVisible(boolean paginationVisible) {
		this.paginationVisible = paginationVisible;
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {		
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		
		int rowId = event.getRow();
		JobSchedulerConfigBean bean = jobSchedulerConfigs.get(rowId);
		this.selectedJobSchedulerConfig = bean.getJobSchedulerConfig();
		
		jobSchedulerCreationBean.update(selectedJobSchedulerConfig);
	}
	
	public void editJobScheduler(ActionEvent event) {
		if (!isAuthorized(107,"")) {
			setAccessDenied();
			return;
		}
		JobSchedulerConfigBean jobSchedulerSelected = (JobSchedulerConfigBean)event.getComponent().getAttributes().get("jobSchedulerConfigBean");
		jobSchedulerCreationBean.update(jobSchedulerSelected.getJobSchedulerConfig());
		jobSchedulerCreationBean.edit();
		
//		int nb = 0;
//		for (JobSchedulerConfigBean bean : jobSchedulerConfigs) {
//			if (bean.isSelected()){
//				nb++;
//			}
//		}
//		
//		if (nb > 0) {
//			if (nb == 1)
//				jobSchedulerCreationBean.edit();
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("Please, select only one Job Scheduler to edit");
//			}
//		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("No Job Scheduler has been selected");
//		}
	}
	
	public void deleteJobScheduler(ActionEvent event) {
		if (!isAuthorized(106,"")) {
			setAccessDenied();
			return;
		}
		JobSchedulerConfigBean jobSchedulerSelected = (JobSchedulerConfigBean)event.getComponent().getAttributes().get("jobSchedulerConfigBean");
		jobSchedulerCreationBean.update(jobSchedulerSelected.getJobSchedulerConfig());
		jobSchedulerCreationBean.delete();
		jobSchedulerConfigsSelected = new ArrayList<JobSchedulerConfigBean>();
		selectAll = false;
		selectedJobSchedulerConfig = null;
	}
	
	public void deleteSelectedJobSchedulers(ActionEvent event) {
		if (!isAuthorized(106,"")) {
			setAccessDenied();
			return;
		}
		for(JobSchedulerConfigBean jobSchedulerConfig : jobSchedulerConfigs) {
			if(jobSchedulerConfig.isSelected()) {
				jobSchedulerCreationBean.update(jobSchedulerConfig.getJobSchedulerConfig());
				jobSchedulerCreationBean.delete();
			}
		}
		jobSchedulerConfigsSelected = new ArrayList<JobSchedulerConfigBean>();
		selectAll = false;
		selectedJobSchedulerConfig = null;
		
//		int nb = 0;
//		for (JobSchedulerConfigBean bean : jobSchedulerConfigs) {
//			if (bean.isSelected()){
//				nb++;
//			}
//		}
//		
//		if (nb > 0) {
//			if (nb == 1) {
//				jobSchedulerCreationBean.delete();
//				selectedJobSchedulerConfig = null;
//			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("Please, select only one Job Scheduler to edit");
//			}
//		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("No Job Scheduler has been selected");
//		}
	}
	
	public void duplicateJobScheduler(ActionEvent event) {
		if (!isAuthorized(108,"")) {
			setAccessDenied();
			return;
		}
		JobSchedulerConfigBean jobSchedulerSelected = (JobSchedulerConfigBean)event.getComponent().getAttributes().get("jobSchedulerConfigBean");
		jobSchedulerCreationBean.update(jobSchedulerSelected.getJobSchedulerConfig());
		jobSchedulerCreationBean.duplicate();
		
//		int nb = 0;
//		for (JobSchedulerConfigBean bean : jobSchedulerConfigs) {
//			if (bean.isSelected()){
//				nb++;
//			}
//		}
//		
//		if (nb > 0) {
//			if(nb == 1)
//				jobSchedulerCreationBean.duplicate();
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("Please, select only one Job Scheduler to edit");
//			}
//		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("No Job Scheduler has been selected");
//		}
	}
	
	public String getDeleteMessage() {
		int nb = 0;
		for (JobSchedulerConfigBean bean : jobSchedulerConfigs) {
			if (bean.isSelected()){
				nb++;
				selectedJobSchedulerConfig = bean.getJobSchedulerConfig();
			}
		}
		String message = "No Job Scheduler selected";
		if (nb == 1) {
			for(JobSchedulerConfigBean jobSchedulerConfig : jobSchedulerConfigs) {
				if(jobSchedulerConfig.isSelected()) {
					message = "Are you sure to delete this Job Scheduler : " + jobSchedulerConfig.getJobSchedulerConfig().getName();
					break;
				}
			}
			return message;
		}
		else {
			message = "Are you sure to delete these " + nb + " Job Schedulers?";
			return message;
		}
	}
	
	public JobSchedulerCreationBean getJobSchedulerCreationBean() {
		return jobSchedulerCreationBean;
	}

	public boolean isRendererFilter() {
		return rendererFilter;
	}
		
	public void openFilterPopup(ActionEvent event) {
		rendererFilter = true;
	}
	
	public void closeFilterPopup(ActionEvent event) {
		rendererFilter = false;
	}

	public LogicalEnvSelectionBean getLogicalEnvSelectionBean() {
		return logicalEnvSelectionBean;
	}

	public IkrJobSchedulerConfig getSelectedJobSchedulerConfig() {
		return selectedJobSchedulerConfig;
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}	

	public String getEnvColumnName() {
		return envColumnName;
	}

	public String getJobTypeColumnName() {
		return jobTypeColumnName;
	}

	public String getJobNameColumnName() {
		return jobNameColumnName;
	}

	public String getModeColumnName() {
		return modeColumnName;
	}
	
	public void handleSelectedJobScheduler(ValueChangeEvent event) {
		JobSchedulerConfigBean jobSchedulerSelected = (JobSchedulerConfigBean)event.getComponent().getAttributes().get("jobSchedulerConfigBean");
		if(jobSchedulerSelected != null) {
			for(JobSchedulerConfigBean jobSchedulerConfig : jobSchedulerConfigs) {
				if(jobSchedulerConfig.equals(jobSchedulerSelected)) {
					jobSchedulerConfig.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						jobSchedulerConfigsSelected.add(jobSchedulerSelected);
					else
						jobSchedulerConfigsSelected.remove(jobSchedulerSelected);
				}
			}
		}
	}
	
	public void handleSelectAllJobSchedulers(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		jobSchedulerConfigsSelected.clear();
		for(JobSchedulerConfigBean jobSchedulerConfig : jobSchedulerConfigs) {
			jobSchedulerConfig.setSelected((Boolean)evt.getNewValue());
			if((Boolean)evt.getNewValue())
				jobSchedulerConfigsSelected.add(jobSchedulerConfig);
		}
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public int getJobSchedulerConfigsSelected() {
		int size = jobSchedulerConfigsSelected.size();
		return size;
	}

	public void setJobSchedulerConfigsSelected(
			List<JobSchedulerConfigBean> jobSchedulerConfigsSelected) {
		this.jobSchedulerConfigsSelected = jobSchedulerConfigsSelected;
	}
	
	public boolean getListRendered() {
		return getJobSchedulerConfigs().size() > 0;
	}

	@Override
	protected void sort() {
		Collections.sort(jobSchedulerConfigs, new Comparator<JobSchedulerConfigBean>() {
			public int compare(JobSchedulerConfigBean o1, JobSchedulerConfigBean o2) {
				int res = 0;
				try {
					if (getEnvColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getLogicalEnv().getName().toLowerCase().compareTo(o2.getLogicalEnv().getName().toLowerCase()) :  o2.getLogicalEnv().getName().toLowerCase().compareTo(o1.getLogicalEnv().getName().toLowerCase());
					}
					else if (getJobTypeColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getJobType().toLowerCase().compareTo(o2.getJobType().toLowerCase()) : o2.getJobType().toLowerCase().compareTo(o1.getJobType().toLowerCase());
					}
					else if (getJobNameColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()) : o2.getName().toLowerCase().compareTo(o1.getName().toLowerCase());
					}
					else if (getModeColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getMode().toLowerCase().compareTo(o2.getMode().toLowerCase()) : o2.getMode().toLowerCase().compareTo(o1.getMode().toLowerCase());
					}
				}
				catch (Exception e) {}
				return res;
			}
		});		
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}	
}
