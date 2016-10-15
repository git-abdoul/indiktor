package com.fsi.monitoring.kpi.units;

public class FormattedValue 
implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1617875068152762325L;

	
	private String value;
	private IkrUnit ikrUnit;
	
	public FormattedValue(String value, IkrUnit ikrUnit) {
		this.value = value;
		this.ikrUnit = ikrUnit;
	}
	
	public String getValue() {
		return value;
	}
	
	public IkrUnit getIkrUnit() {
		return ikrUnit;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setIkrUnit(IkrUnit ikrUnit) {
		this.ikrUnit = ikrUnit;
	}		
}
