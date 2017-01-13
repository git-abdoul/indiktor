package com.fsi.monitoring.computeServer.test;

import org.junit.Test;
import org.nfunk.jep.JEP;


public class ComputeServerMainTest {
	

	
	@Test
	public void jepComputation() throws Exception {
				
		try {

			String[] conditions = {"c1-0","c2-1","c3-0"};
			String expr = "";
			for (int i=0;i<conditions.length;i++) {expr = expr + conditions[i] + "  ";}
			
			String cause = "C1";
			double res = testCompute(conditions, cause);
			assert res==0: "Cause=" + cause +", " + expr;

			cause = "C1 and C2";
			res = testCompute(conditions, cause);
			assert res==0: "Cause=" + cause +", " + expr;

			cause = "C1 or C2";
			res = testCompute(conditions, cause);
			assert res==1: "Cause=" + cause +", " + expr;
			
			cause = "C1 Or C2";
			res = testCompute(conditions, cause);
			assert res==1: "Cause=" + cause +", " + expr;
			
			cause = "(C1 or C2) AND c2";
			res = testCompute(conditions, cause);
			assert res==1: "Cause=" + cause +", " + expr;
			
		} catch (Exception e) {
		     System.out.println("An error occurred: " + e.getMessage());
		}					
	}
	
	private double testCompute(String[] conditions, String cause) {
		JEP jep = new JEP();
		
		for (int j=0;j<conditions.length;j++) {
			String[] cond = conditions[j].split("-");
			jep.addVariable(cond[0], Integer.parseInt(cond[1]));
		}
					
		cause = cause.toLowerCase().replaceAll("and", "&&").replaceAll("or", "||");			
		jep.parseExpression(cause);
		double doubleRes =  jep.getValue();
		return doubleRes;
	}
	
}
