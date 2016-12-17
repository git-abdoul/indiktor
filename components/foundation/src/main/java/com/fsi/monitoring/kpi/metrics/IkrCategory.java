package com.fsi.monitoring.kpi.metrics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.kpi.units.IkrUnitType;
import com.fsi.monitoring.kpi.units.StorageUnit;

public class IkrCategory
extends IkrStaticDomain
implements Serializable {

	private static final long serialVersionUID = 6232024427029059484L;
	
	private IkrUnitType ikrUnitType;
	private IkrUnit ikrUnit;
	
	private double threshold;
	private boolean persistent;
	private boolean archive;
	private List<String> searchesIndexes;
	
	public IkrCategory(int parentDomainId) {
		super(parentDomainId);
		
		// Random default values for a new IkrCategory
		ikrUnitType = IkrUnitType.STORAGE;
		ikrUnit = StorageUnit.BYTE;
		
		threshold = 0;
		persistent = true;
		archive = true;
		
		searchesIndexes = new ArrayList<String>();
	}	
	
	public IkrCategory(int id,
					   int parentDomainId, 
					   String domainValue, 
					   String label, 
					   String description,
		 	   		   IkrUnitType ikrUnitType,
		 	   		   IkrUnit ikrUnit,
		 	   		   double threshold,
		 	   		   boolean persistent,
		 	   		   boolean archive,
		 	   		   List<String> searchesIndexes) {	
		super(id,parentDomainId,domainValue,label,description);
		this.ikrUnitType = ikrUnitType;
		this.ikrUnit = ikrUnit;
		this.threshold = threshold;
		this.persistent = persistent;
		this.archive = archive;
		this.searchesIndexes = searchesIndexes;
	}
	
	public IkrUnitType getIkrUnitType() {
		return ikrUnitType;
	}

	public IkrUnit getIkrUnit() {
		return ikrUnit;
	}

	public void setIkrUnitType(IkrUnitType ikrUnitType) {
		this.ikrUnitType = ikrUnitType;
	}

	public void setIkrUnit(IkrUnit ikrUnit) {
		this.ikrUnit = ikrUnit;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	public boolean isArchive() {
		return archive;
	}

	public void setArchive(boolean archive) {
		this.archive = archive;
	}

	public List<String> getSearchesIndexes() {
		return searchesIndexes;
	}

	public void setSearchesIndexes(List<String> searchesIndexes) {
		this.searchesIndexes = searchesIndexes;
	}	
}
