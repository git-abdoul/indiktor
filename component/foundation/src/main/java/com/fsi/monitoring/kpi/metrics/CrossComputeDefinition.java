package com.fsi.monitoring.kpi.metrics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fsi.monitoring.kpi.compute.MetricCompute;

public class CrossComputeDefinition
extends AbstractIkrDefinition
implements Serializable {
	
	private static final long serialVersionUID = 4929210202698825378L;
	
	public static final String CROSS_COMPUTE_CONTEXT = "COMPUTED";
	
	private int logicalEnvId;
	private String crossComputation;
	
	public CrossComputeDefinition() {
		super();
	}
	
	public CrossComputeDefinition(long id,
								  int logicalEnvId,
								  int ikrCategoryId,
								  String ikrInstance,
								  MetricCompute ikrCompute,
								  String crossComputation,
								  boolean isActivated) {
		super(id, ikrCategoryId, ikrInstance, ikrCompute, isActivated);
		this.crossComputation = crossComputation;
		this.logicalEnvId = logicalEnvId;
	}

	public String getCrossComputation() {
		return crossComputation;
	}

	public void setCrossComputation(String crossComputation) {
		this.crossComputation = crossComputation;
	}

	public int getLogicalEnvId() {
		return logicalEnvId;
	}

	public void setLogicalEnvId(int logicalEnvId) {
		this.logicalEnvId = logicalEnvId;
	}
	
	public static Collection<Long> parse(String formula) {
		Collection<Long> res = new ArrayList<Long>();
		Pattern p = Pattern.compile("M\\d+");
		Matcher m = p.matcher(formula);
		boolean b = false;
		while(b = m.find()) {
			String formulaId = m.group();
			
			String defIdStr = formulaId.substring(1);
			Long defId = Long.valueOf(defIdStr);
			
			res.add(defId);	
		}
		return res;
	}
	
	public Collection<Long> parse() {
		return parse(this.crossComputation);
	}
}
