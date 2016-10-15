package com.fsi.monitoring.alert.config.definition;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.monitoring.AlertConditionOperator;
import com.fsi.monitoring.alert.condition.AlertCondition;
import com.fsi.monitoring.alert.condition.ValueAlertCondition;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.kpi.units.IkrUnitType;


public class AlertConditionBean implements Serializable {
	private static final Logger logger = Logger.getLogger(AlertConditionBean.class);		
	private static final long serialVersionUID = -7642486855077723871L;
	
	private int id;
	private IkrDefinitionBean ikrDefinitionBean;
	private AbstractIkrDefinition ikrDefinition;
	private IkrCategory ikrCategory;
	
	private AlertConditionOperator operator;
	private String value;
	private IkrUnitType ikrUnitType;
	private IkrUnit unit;

	private static SelectItem[] allOperatorItems;
	private static SelectItem[] booleanOperatorItems;
	private static SelectItem[] stringOperatorItems;
	private static SelectItem[] datetimeOperatorItems;
	
	private SelectItem[] operators;
	
	static {
		AlertConditionOperator[] allOperators = AlertConditionOperator.values();

		allOperatorItems = new SelectItem[] {	
				new SelectItem(allOperators[0],allOperators[0].getDisplay()),
				new SelectItem(allOperators[1],allOperators[1].getDisplay()),
				new SelectItem(allOperators[2],allOperators[2].getDisplay()),
				new SelectItem(allOperators[3],allOperators[3].getDisplay()),
				new SelectItem(allOperators[4],allOperators[4].getDisplay()),
				new SelectItem(allOperators[5],allOperators[5].getDisplay()),
		};
	
		booleanOperatorItems = new SelectItem[] {	
				new SelectItem(AlertConditionOperator.EQUAL_TO,AlertConditionOperator.EQUAL_TO.getDisplay()),
		};
		
		stringOperatorItems = new SelectItem[] {	
				new SelectItem(AlertConditionOperator.EQUAL_TO,AlertConditionOperator.EQUAL_TO.getDisplay()),
				new SelectItem(AlertConditionOperator.NOT_EQUAL_TO,AlertConditionOperator.NOT_EQUAL_TO.getDisplay()),
				new SelectItem(AlertConditionOperator.CONTAINS,AlertConditionOperator.CONTAINS.getDisplay()),
				new SelectItem(AlertConditionOperator.NOT_CONTAINS,AlertConditionOperator.NOT_CONTAINS.getDisplay()),
		};
		
		datetimeOperatorItems = new SelectItem[] {	
				new SelectItem(AlertConditionOperator.EQUAL_TO,AlertConditionOperator.EQUAL_TO.getDisplay()),
				new SelectItem(AlertConditionOperator.NOT_EQUAL_TO,AlertConditionOperator.NOT_EQUAL_TO.getDisplay()),
				new SelectItem(AlertConditionOperator.GREATER_THAN,AlertConditionOperator.GREATER_THAN.getDisplay()),
				new SelectItem(AlertConditionOperator.GREATER_THAN_OR_EQUAL_TO,AlertConditionOperator.GREATER_THAN_OR_EQUAL_TO.getDisplay()),
				new SelectItem(AlertConditionOperator.LESS_THAN,AlertConditionOperator.LESS_THAN.getDisplay()),
				new SelectItem(AlertConditionOperator.LESS_THAN_OR_EQUAL_TO,AlertConditionOperator.LESS_THAN_OR_EQUAL_TO.getDisplay()),
		};
	}	
	
	public AlertConditionBean(AlertCondition alertCondition, IkrDefinitionBean ikrDefinitionBean) {
		
		
		this(alertCondition.getId(),ikrDefinitionBean);
		
		this.operator = alertCondition.getOperator();
		
		ValueAlertCondition vac = (ValueAlertCondition)alertCondition;
		this.value = vac.getValue();		
		this.ikrUnitType = vac.getUnitType();
		this.unit = vac.getUnit();
	}
	
	public AlertConditionBean duplicate() {
		AlertConditionBean alertConditionBean = new AlertConditionBean(0,ikrDefinitionBean);		
		alertConditionBean.operator = operator;
		alertConditionBean.value = value;
		alertConditionBean.ikrUnitType = ikrUnitType;
		alertConditionBean.unit = unit;
		alertConditionBean.ikrCategory = ikrCategory;
		alertConditionBean.ikrDefinition = ikrDefinition;		
		return alertConditionBean;
	}
	
	
	public AlertConditionBean(int alertConditionId, IkrDefinitionBean ikrDefinitionBean) {
		this.ikrDefinitionBean = ikrDefinitionBean;
		this.id = alertConditionId;
		this.ikrCategory = ikrDefinitionBean.getIkrCategory();
		this.ikrDefinition = ikrDefinitionBean.getIkrDefinition();
		
		this.ikrUnitType = ikrCategory.getIkrUnitType();
		this.unit = ikrCategory.getIkrUnit();
		
		initOperators();
		operator = (AlertConditionOperator)operators[0].getValue();
//
		ikrDefinitionBean = new IkrDefinitionBean(ikrDefinition, ikrCategory);
	}
	
	public AlertCondition validate() {
		AlertCondition alertCondition = new ValueAlertCondition(id, true, ikrDefinition.getId(), value, ikrUnitType, unit);
		alertCondition.setOperator(operator);
		return alertCondition;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public IkrCategory getIkrCategory() {
		return ikrCategory;
	}
	
	public AbstractIkrDefinition getIkrDefinition() {
		return ikrDefinition;
	}
	
	public IkrDefinitionBean getIkrDefinitionBean() {
		return ikrDefinitionBean;
	}

	public SelectItem[] getOperators() {
		return operators;
	}
	
	private void initOperators() {		
		IkrUnitType ikrUnitType = ikrCategory.getIkrUnitType();
		
		switch (ikrUnitType) {
			case BOOLEAN :
				operators = booleanOperatorItems;
			break;
		
			case DURATION :
			case RATE :
			case STORAGE :
			case THROUGHPUT :
			case NUMBER :
				operators = allOperatorItems;
			break;
			
			case STRING :
				operators = stringOperatorItems;
			break;
			
			case DATETIME :
				operators = datetimeOperatorItems;
			break;
		}
	}
	
	public AlertConditionOperator getOperator() {
		return operator;
	}
	
	public void setOperator(AlertConditionOperator operator) {
		this.operator = operator;
	}
	
	public SelectItem[] getUnits() {
		IkrUnitType unitType = ikrCategory.getIkrUnitType();
		if (unitType == null) {
			return new SelectItem[0];
		}
		
		Collection<IkrUnit> ikrUnits = unitType.getIkrUnits();
		SelectItem[] items = new SelectItem[ikrUnits.size()];
		
		int i = 0;
		for(IkrUnit ikrUnit : ikrUnits) {
			items[i++] = new SelectItem(ikrUnit.name(),ikrUnit.getSymbol());
		}
		return items;
	}	
	
	public boolean isUnitsRendered() {
		IkrUnitType unitType = ikrCategory.getIkrUnitType();
		if (unitType == null) {
			return false;
		}

		Collection<IkrUnit> units = unitType.getIkrUnits();
		
		return (units != null && units.size() > 1);
	}
	
    public void updateValue(ValueChangeEvent event) {
    	String newValue = (String)event.getNewValue();
    	this.value = newValue;
    }
	
	public void setValue(String value) {}
	
	public String getValue() {
		return value;
	}
	
	public void setUnit(String unitName) {}
	
    public void updateUnit(ValueChangeEvent event) {
    	String unitName = (String)event.getNewValue();
		IkrUnitType ikrUnitType = ikrCategory.getIkrUnitType();		
		this.unit = ikrUnitType.getIkrUnit(unitName);
    }
    
	public String getUnit() {
		if (unit != null) {
			return unit.name();
		}
		return null;
	}
	
	public String getUnitSymbol() {
		if (unit != null) {
			return unit.getSymbol();
		}
		return null;
	}

	public String getIkrMetricLabel() {
		String ikrMetricLabel = "No IkrDefinition generated";
		if(ikrDefinition!=null) {
			ikrMetricLabel = ikrDefinition.getFullIkrInstance();
		}
		return ikrMetricLabel;
	}
}
