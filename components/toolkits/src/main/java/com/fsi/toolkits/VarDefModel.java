package com.fsi.toolkits;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class VarDefModel implements Serializable {
	private static final long serialVersionUID = 4901275578056937923L;
	
	private Map<String, String> vars;	
	
	
	public VarDefModel() {
		vars = new HashMap<String, String>();
	}

	public void addVar(VariableModel model) {
		vars.put(model.getName(), model.getValue());
	}

	public Map<String, String> getVars() {
		return vars;
	}
}
