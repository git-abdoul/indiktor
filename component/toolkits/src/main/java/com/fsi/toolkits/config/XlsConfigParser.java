package com.fsi.toolkits.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public abstract class XlsConfigParser {
	
	protected String toolkitConfigHome;
	protected String toolkitResourcesHome;
	
	protected Properties properties;	
	protected String logicalEnvName;	
	
	public XlsConfigParser(String logicalEnvName) {
		this.logicalEnvName = logicalEnvName;			
		toolkitConfigHome = System.getProperty("toolkit.conf");
		toolkitResourcesHome = System.getProperty("toolkit.resources");
	}
	
	public void parse() {
		String propertiesPathName = toolkitConfigHome + File.separator + logicalEnvName + ".properties";		
		
		try {  			
			properties = new Properties();
			properties.load(new FileInputStream(propertiesPathName));
			
			parseDocument();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected abstract void parseDocument();

	protected List<String[]> getRowValues(HSSFSheet sheet) {
		List<String[]> rowValues = new ArrayList<String[]>();
		for (int j = 1; j < sheet.getLastRowNum() + 1; j++) {
			HSSFRow row = sheet.getRow(j);
			if (row == null)
				continue;
			int sz = row.getLastCellNum();
			String[] values = new String[sz];
			for (int i = 0; i < sz; i++) {
				HSSFCell cell = row.getCell((short) i);
				if (cell != null) {	
					if (HSSFCell.CELL_TYPE_STRING == cell.getCellType())
						values[i] = cell.getRichStringCellValue().getString();
					else if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {			
						values[i] = String.valueOf(cell.getNumericCellValue());
					}
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
	
	protected String getValue(String value, String template) {
		String res = value;
		int i = 1;
		boolean stopCompute = false;
		while (!stopCompute) {
			String varKey = template + "_" + i;
			String varVal = properties.getProperty(varKey);
			if (varVal == null) {
				stopCompute = true;
			}
			else {
				res = res.replace("%"+varKey+"%", varVal);
			}
			
			i = i + 1;
		}
		
		return res;
	}

}
