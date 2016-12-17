package com.fsi.monitoring.util;

import java.util.ArrayList;
import java.util.List;

public class IkrUtils {
	protected static final String ALL_WILDCARD ="*";
	
	public static List<String> splitSearchIndex(String index) {
		List<String> values = new ArrayList<String>();
		String[] tokens = index.split(" ");
		for (String tok : tokens) {
			if (tok.length()>0) {
				values.add(tok);
			}
		}
		return values;
	}
	
	/**
	 * @return true if the name contains one the component of the filter
	 */
	public static boolean accepts(String name, String[] filter){
		if(filter==null || filter.length==0)
			return false;
		if(name==null || name.length()==0)
			return false;
		String filterComponent;
		boolean accepted = false;
		int filterInd = 0;
		while(!accepted && filterInd<filter.length){
			filterComponent = filter[filterInd];
			accepted = name.contains(filterComponent) || filterComponent.equals(ALL_WILDCARD);
			filterInd++;
		}		
		return accepted;
	}
	
	/**
	 * @return true if the name contains one the component of the filter
	 */
	public static boolean accepts(String[] names, String filter){
		if(filter==null || filter.length()==0)
			return false;
		if(names==null || names.length==0)
			return false;
		String nameComponent;
		boolean accepted = false;
		int nameInd = 0;
		while(!accepted && nameInd<names.length){
			nameComponent = names[nameInd];
			accepted = nameComponent.contains(filter) || filter.equals(ALL_WILDCARD);
			nameInd++;
		}		
		return accepted;
	}
	
	/**
	 * @return true if the name contains one the component of the filter
	 */
	public static boolean accepts(String[] names, String[] filter, boolean isReverse, boolean isAndClause){
		String[] newNames = names;
		String[] newFilter = filter;
		if (isReverse) {
			newNames = filter;
			newFilter = names;
		}
			
		boolean accept = false;
		for(int i=0; i<newNames.length; i++) {
			boolean ret = false;
			if (isReverse)
				ret = accepts(newFilter, newNames[i]);
			else
				ret = accepts(newNames[i], newFilter);
			if (ret == true && !isAndClause) {
				accept = true;
				break;
			}
			else {
				if (i==0)
					accept = ret;
				else
					accept = ret && accept;
				
			}
		}
		return accept;
	}

}
