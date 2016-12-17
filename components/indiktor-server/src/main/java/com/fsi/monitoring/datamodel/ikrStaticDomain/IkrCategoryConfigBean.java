package com.fsi.monitoring.datamodel.ikrStaticDomain;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.jfree.util.Log;

import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.kpi.units.IkrUnitType;
import com.fsi.monitoring.util.AccessControlBean;


public class IkrCategoryConfigBean extends AccessControlBean{

	private IkrCategory ikrCategory;
	
	private boolean rendererThreshold = true;
	private boolean enableArchive = true;

	private boolean deleteConfirmation = false;
	
	private String searchIndexes;

	
	public IkrCategoryConfigBean() {}
	
	public void setIkrCategory(IkrCategory ikrCategory) {
		this.ikrCategory = ikrCategory;
		List<String> oldSearchIndexes = ikrCategory.getSearchesIndexes();
		int sz = oldSearchIndexes.size();
        String indexeStr = "";
        int i = 0;
        for (String index : oldSearchIndexes) {
        	indexeStr  = indexeStr + index;
        	if (i < sz-1) {
        		indexeStr  = indexeStr + ":";
        	}
        	i++;
        }
        searchIndexes = indexeStr;
	}
	
	public IkrCategory getIkrCategory() {
		return ikrCategory;
	}
	
	public IkrUnitType getIkrUnitType() {
		return ikrCategory.getIkrUnitType();
	}
	
	public void setIkrUnitType(IkrUnitType ikrUnitType) {}

	public void onChangeIkrUnitType(ValueChangeEvent e) {
		IkrUnitType newValue = (IkrUnitType)e.getNewValue();	
		ikrCategory.setIkrUnitType(newValue);	
		
		List<IkrUnit> ikrUnits = newValue.getIkrUnits();
		changeIkrUnit(ikrUnits.get(0).name());
	}	
	
	public SelectItem[] getIkrUnitTypes() {
		EnumSet<IkrUnitType> unitTypes = EnumSet.allOf(IkrUnitType.class);
		
		SelectItem[] items = new SelectItem[unitTypes.size()-1];
		
		int i = 0;
		for(IkrUnitType unitType : unitTypes) {
			if (unitType != IkrUnitType.NA)
				items[i++] = new SelectItem(unitType,unitType.name());
		}
		return items;
	}
	
	public SelectItem[] getBooleanSelection() {
		SelectItem[] items = new SelectItem[2];		
		items[0] = new SelectItem(true, "true");
		items[1] = new SelectItem(false, "false");
		return items;
	}
	
	public String getIkrUnit() {
		IkrUnit ikrUnit = ikrCategory.getIkrUnit();
		
		if (ikrUnit != null) {
			return ikrUnit.name();
		}
		
		return null;
	}
	
	public SelectItem[] getIkrUnits() {
		IkrUnitType unitType = ikrCategory.getIkrUnitType();
		if (unitType == null) {
			return new SelectItem[0];
		}
		
		Collection<IkrUnit> ikrUnits = unitType.getIkrUnits();
		
		SelectItem[] items = null;
		if (ikrUnits != null) {
			items = new SelectItem[ikrUnits.size()];
		
			int i = 0;
			for(IkrUnit ikrUnit : ikrUnits) {
				items[i++] = new SelectItem(ikrUnit.name(),ikrUnit.getSymbol());
			}
		}
		return items;
	}	
	
	public void onPersistentValueChanged (ValueChangeEvent e) {
		boolean newValue = (Boolean)e.getNewValue();
		ikrCategory.setPersistent(newValue);
		ikrCategory.setArchive(newValue);
		enableArchive = newValue;
	}
	
	public void onChangeIkrUnit(ValueChangeEvent e) {
		String unit = (String)e.getNewValue();
		changeIkrUnit(unit);
	}
	
	private void changeIkrUnit(String unit) {
		if (unit != null && unit.length() > 0) {
			try {
				IkrUnit ikrUnit = ikrCategory.getIkrUnitType().getIkrUnit(unit);
				ikrCategory.setIkrUnit(ikrUnit);
			} catch (Exception exc) {
				Log.error("Invalid IkrUnit " + unit);
			}
		}		
	}
	
	public void setIkrUnit(String unit) {}
	
	public boolean isUnitsRendered() {
		IkrUnitType unitType = ikrCategory.getIkrUnitType();
		if (unitType == null) {
			return false;
		}

		Collection<IkrUnit> units = unitType.getIkrUnits();
		
		return (units != null && units.size() > 1);
	}
	
	public void toggleDeleteConfirmation(ActionEvent event) {
		if (!isAuthorized(22, "ikrDomainConfig")) {
			return;
		}
		
		deleteConfirmation = !deleteConfirmation;
    }

	public boolean isDeleteConfirmation() {
		return deleteConfirmation;
	}

	public boolean isRendererThreshold() {
		switch (ikrCategory.getIkrUnitType()) {		
			case NUMBER :
			case RATE :
			case STORAGE :
			case CURRENCY :
			case THROUGHPUT :
			case DURATION :
				rendererThreshold = true;
			break;
			
			case STRING :
			case BOOLEAN :
			case DATETIME :
				rendererThreshold = false;
			break;
		}		
		return rendererThreshold;
	}	
	
	public boolean isRendererArchiveSelection() {
		return (isEnableArchive() && ikrCategory.isPersistent());
	}
	
	public boolean isEnableArchive() {
		if (!ikrCategory.isPersistent())
			return false;
		
		switch (ikrCategory.getIkrUnitType()) {		
			case NUMBER :
			case RATE :
			case STORAGE :
			case CURRENCY :
			case THROUGHPUT :
			case DURATION :
				enableArchive = true;
			break;
			
			case STRING :
				ikrCategory.setArchive(false);
				enableArchive = false;
			break;
				
			case BOOLEAN :
			case DATETIME :
				enableArchive = true;
			break;
		}		
		return enableArchive;
	}

	public String getSearchIndexes() {
		return searchIndexes;
	}

	public void setSearchIndexes(String searchIndexes) {
		this.searchIndexes = searchIndexes;
	}	
	
	
}
