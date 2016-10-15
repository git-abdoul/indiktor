package com.fsi.monitoring.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.fsi.monitoring.alert.AlertDomain;
import com.fsi.monitoring.alert.AlertGroup;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.AlertSubDomain;
import com.fsi.monitoring.config.PersistencyBeanName;


public class XlsAlertParser {
	public static final String RESOURCE_PATH = "/WEB-INF/classes/";
	
	protected final static Logger logger = Logger.getLogger(XlsAlertParser.class);		
	
	// key = groupName
	private Map<String, Integer> groups;	
	// key = groupName_domainName
	private Map<String, Integer> domains;
	// key = groupName_domainName_subDomainName
	private Map<String, Integer> subdomains;	
	
	private Map<String, List<AlertDefaultDefinition>> alertDefinitions;
	
	public XlsAlertParser() {
		alertDefinitions = new HashMap<String, List<AlertDefaultDefinition>>();
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		InputStream stream = ec.getResourceAsStream(RESOURCE_PATH + "AlertsDef_Template.xls");
		POIFSFileSystem fs;
		init();
		try {
			fs = new POIFSFileSystem(stream);
			parse(new HSSFWorkbook(fs));
		} catch (IOException e) {
			System.err.println("Impossible to process file : " + e.getMessage());
			System.out.println(e);
		}		
	}

	private void parse(HSSFWorkbook workbook) {	
		loadAlertDefinitions(workbook);
		Map<Long, List<AlertDefaultConditions>> alertConditions = loadAlertConditions(workbook);
		Map<Long, List<AlertDefaultComputation>> alertComputations = loadAlertComputations(workbook);
		for(String type : alertDefinitions.keySet()) {
			for(AlertDefaultDefinition def : alertDefinitions.get(type)) {
				long alertDefId = def.getId();
				def.setConditions(alertConditions.get(alertDefId));
				def.setComputations(alertComputations.get(alertDefId));
			}
		}
	}
	
	private void init() {
		AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
		initGroups(alertPM);
		initDomains(alertPM);
		initSubDomains(alertPM);
	}
	
	private void initGroups(AlertPM alertPM) {
		try {
			groups = new HashMap<String, Integer>();
			Map<Integer, AlertGroup> tmp = alertPM.getAllAlertGroups();
			for(int id : tmp.keySet()){
				AlertGroup group = tmp.get(id);
				groups.put(group.getValue(), id);
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	private void initDomains(AlertPM alertPM) {
		try {
			domains = new HashMap<String, Integer>();
			Map<Integer, AlertGroup> groupsTmp = alertPM.getAllAlertGroups();
			Map<Integer, AlertDomain> tmp = alertPM.getAllAlertDomains();
			for(int id : tmp.keySet()){
				AlertDomain domain = tmp.get(id);
				AlertGroup group = groupsTmp.get(domain.getGroupId());
				String key = group.getValue() + "_" + domain.getValue();
				domains.put(key, id);
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	private void initSubDomains(AlertPM alertPM) {
		try {
			subdomains = new HashMap<String, Integer>();
			Map<Integer, AlertGroup> groupsTmp = alertPM.getAllAlertGroups();
			Map<Integer, AlertDomain> domainsTmp = alertPM.getAllAlertDomains();
			Map<Integer, AlertSubDomain> tmp = alertPM.getAllAlertSubDomains();
			for(int id : tmp.keySet()){
				AlertSubDomain subDomain = tmp.get(id);
				AlertDomain domain = domainsTmp.get(subDomain.getDomainId());
				AlertGroup group = groupsTmp.get(domain.getGroupId());
				String key = group.getValue() + "_" + domain.getValue() + "_" + subDomain.getValue();
				subdomains.put(key, id);
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	private void loadAlertDefinitions(HSSFWorkbook workbook){		
		HSSFSheet sheet = workbook.getSheetAt(0);
		List<Object[]> rowValues = getRowValues(sheet);
		for (Object[] values : rowValues) {
			String idStr = Integer.toString((int)((Double)values[0]).doubleValue());
			long alertDefId = Long.valueOf(idStr);
			String type = (String)values[1];
			String name = (String)values[2];			
			int group = groups.get(values[3]);
			int domain = domains.get(values[3]+"_"+values[4]);
			int subdomain = subdomains.get(values[3]+"_"+values[4]+"_"+values[5]);
			String desc = (String)values[6];		
			boolean active = Boolean.valueOf((String)values[7]).booleanValue();
			List<AlertDefaultDefinition> defs = alertDefinitions.get(type);
			if(defs == null) {
				defs = new ArrayList<AlertDefaultDefinition>();
				alertDefinitions.put(type, defs);
			}
			defs.add(new AlertDefaultDefinition(alertDefId, name, group, domain, subdomain, "%MONITOR_ENV%", desc, active));			
		}
	}
	
	private Map<Long, List<AlertDefaultConditions>> loadAlertConditions(HSSFWorkbook workbook) {
		Map<Long, List<AlertDefaultConditions>> conditionMap = new HashMap<Long, List<AlertDefaultConditions>>();
		HSSFSheet sheet = workbook.getSheetAt(1);
		List<Object[]> rowValues = getRowValues(sheet);
		for (Object[] values : rowValues) {	
			String alertDefIdStr = Integer.toString((int)((Double)values[0]).doubleValue());
			String conditionIdStr = Integer.toString((int)((Double)values[1]).doubleValue());
			String ikrCat = (String)values[2];
			String ikrInst = (String)values[3];
			if (ikrCat.contains("process.system"))
				ikrInst = "Start"+ikrInst;
			String ikrEnv = (String)values[4];	
			double valMin = (Double)values[5];
			double valMax = (Double)values[6];
			boolean active = Boolean.valueOf((String)values[7]).booleanValue();
			List<AlertDefaultConditions> conds = conditionMap.get(Long.valueOf(alertDefIdStr));
			if(conds == null) {
				conds = new ArrayList<AlertDefaultConditions>();
				conditionMap.put(Long.valueOf(alertDefIdStr), conds);
			}
			conds.add(new AlertDefaultConditions(Long.valueOf(alertDefIdStr), Integer.valueOf(conditionIdStr), -1, valMin, valMax,active, ikrCat, ikrInst, ikrEnv));
		}
		return conditionMap;
	}
	
	private Map<Long, List<AlertDefaultComputation>> loadAlertComputations(HSSFWorkbook workbook) {
		Map<Long, List<AlertDefaultComputation>> computationMap = new HashMap<Long, List<AlertDefaultComputation>>();
		HSSFSheet sheet = workbook.getSheetAt(2);
		List<Object[]> rowValues = getRowValues(sheet);
		for (Object[] values : rowValues) {	
			String alertDefIdStr = Integer.toString((int)((Double)values[0]).doubleValue());
			String levelStr = Integer.toString((int)((Double)values[1]).doubleValue());
			String cause = (String)values[2];
			String label = (String)values[3];
			boolean active = Boolean.valueOf((String)values[4]).booleanValue();
			long alertDefId = Long.valueOf(alertDefIdStr);
			List<AlertDefaultComputation> comps = computationMap.get(Long.valueOf(alertDefIdStr));
			if (comps == null) {
				comps = new ArrayList<AlertDefaultComputation>();
				computationMap.put(alertDefId, comps);
			}
			comps.add(new AlertDefaultComputation(alertDefId,Integer.valueOf(levelStr), cause, label, active));
		}
		return computationMap;
	}	

	private List<Object[]> getRowValues(HSSFSheet sheet) {
		List<Object[]> rowValues = new ArrayList<Object[]>();
		for (int j = 1; j < sheet.getLastRowNum() + 1; j++) {
			HSSFRow row = sheet.getRow(j);
			if (row == null)
				continue;
			int sz = row.getLastCellNum();
			Object[] values = new Object[sz];
			for (int i = 0; i < sz; i++) {
				HSSFCell cell = row.getCell((short) i);
				if (cell != null) {	
					if (HSSFCell.CELL_TYPE_STRING == cell.getCellType())
						values[i] = cell.getRichStringCellValue().getString();
					else if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType())				
						values[i] = cell.getNumericCellValue();
					else
						values[i] = "";
				}
				else 
					values[i] = "";		
			}
			rowValues.add(values);
		}
		
		return rowValues;
	}	
	
	public Map<String, List<AlertDefaultDefinition>> getAlertDefaultDefinitions() {
		return alertDefinitions;
	}
	
	public class AlertDefaultDefinition {
		private long id;
		private int group, domain, subdomain;
		private String name, env, desc;
		private boolean active;
		private List<AlertDefaultConditions> conditions;
		private List<AlertDefaultComputation> computations;
		
		public AlertDefaultDefinition(long id, String name, int group, int domain, int subdomain, String env, String desc,	boolean active) {
			super();
			this.id = id;
			this.group = group;
			this.domain = domain;
			this.subdomain = subdomain;
			this.env = env;
			this.name = name;
			this.desc = desc;
			this.active = active;
			conditions = new ArrayList<AlertDefaultConditions>();
			computations = new ArrayList<AlertDefaultComputation>();
		}

		public long getId() {
			return id;
		}
		
		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public String getDesc() {
			return desc;
		}

		public boolean isActive() {
			return active;
		}

		public List<AlertDefaultConditions> getConditions() {
			return conditions;
		}

		public void setConditions(List<AlertDefaultConditions> conditions) {
			this.conditions = conditions;
		}	
		
		public void addcondition(AlertDefaultConditions cond) {
			this.conditions.add(cond);
		}
		
		public List<AlertDefaultComputation> getComputations() {
			return computations;
		}

		public void setComputations(List<AlertDefaultComputation> computations) {
			this.computations = computations;
		}	
		
		public void addComputation(AlertDefaultComputation compute) {
			this.computations.add(compute);
		}

		public int getGroup() {
			return group;
		}

		public int getDomain() {
			return domain;
		}

		public int getSubdomain() {
			return subdomain;
		}

		public String getEnv() {
			return env;
		}
	}
	
	public class AlertDefaultConditions {
		private int conditionId;
		private long alertId, ikrDefId;
		private double valMin, valMax;
		private boolean active;
		private String category,instance, env; 

		public AlertDefaultConditions(long alertId, int conditionId, long ikrDefId, double valMin,
				double valMax, boolean active, String category,
				String instance, String env) {
			super();
			this.alertId = alertId;
			this.conditionId = conditionId;
			this.ikrDefId = ikrDefId;
			this.valMin = valMin;
			this.valMax = valMax;
			this.active = active;
			this.category = category;
			this.instance = instance;
			this.env = env;
		}

		public long getAlertId() {
			return alertId;
		}

		public long getIkrDefId() {
			return ikrDefId;
		}		

		public int getConditionId() {
			return conditionId;
		}

		public double getValMin() {
			return valMin;
		}

		public double getValMax() {
			return valMax;
		}

		public boolean isActive() {
			return active;
		}

		public String getCategory() {
			return category;
		}

		public String getInstance() {
			return instance;
		}

		public String getEnv() {
			return env;
		}

		public void setIkrDefId(long ikrDefId) {
			this.ikrDefId = ikrDefId;
		}
		
	}
	
	public class AlertDefaultComputation {
		private long alertId;
		private int level;
		private String cause, label;
		private boolean enable;
		
		public AlertDefaultComputation(long alertId, int level, String cause,
				String label, boolean enable) {
			super();
			this.alertId = alertId;
			this.level = level;
			this.cause = cause;
			this.label = label;
			this.enable = enable;
		}

		public long getAlertId() {
			return alertId;
		}

		public int getLevel() {
			return level;
		}

		public String getCause() {
			return cause;
		}

		public String getLabel() {
			return label;
		}

		public boolean isEnable() {
			return enable;
		}		
	}
}
