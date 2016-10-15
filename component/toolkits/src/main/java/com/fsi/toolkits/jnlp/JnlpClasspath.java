package com.fsi.toolkits.jnlp;

import java.util.ArrayList;
import java.util.List;

public class JnlpClasspath {
	private List<JnlpJarModel> jars;	
	
	public JnlpClasspath() {
		jars = new ArrayList<JnlpJarModel>();
	}
	
	public void addJarModel(JnlpJarModel model) {
		jars.add(model);
	}		
	
	public List<JnlpJarModel> getJars() {
		return jars;
	}

}
