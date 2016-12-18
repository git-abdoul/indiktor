package com.fsi.monitoring.util.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;

public class IkrExcelExport {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public static void writeGridAsExcel(File file, Map<Long, List<IkrValueBean>> valueBeans) {	
		FileOutputStream out = null;
		try {  
			HSSFWorkbook workbook = new HSSFWorkbook();
			int i = 0;
			for (List<IkrValueBean> beans : valueBeans.values()) {
				if(beans.size()>0) {
					createSheet(workbook, beans, i);
					i++;
				}
			}
			out = new FileOutputStream(file);
			workbook.write(out);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void createSheet(HSSFWorkbook workbook, List<IkrValueBean> beans, int pos) {
		IkrValueBean bean = beans.get(0);
		IkrCategory cat = bean.getIkrDefinitionBean().getIkrCategory();
		AbstractIkrDefinition def = bean.getIkrDefinitionBean().getIkrDefinition();
		String name = cat.getLabel() + " : " + def.getIkrInstance();
		String unitTmp = bean.getFormattedValue().getIkrUnit().getSymbol();
		if (unitTmp!=null && unitTmp.length()>0)
			name = name + " (" + unitTmp + ")";
		HSSFSheet sheet = workbook.createSheet(cat.getLabel());
		
		HSSFRow firstRow = sheet.createRow((short)0);
		firstRow.createCell((short)0).setCellValue(new HSSFRichTextString(name));
		
		int i = 2;
		for(IkrValueBean value : beans) {
			HSSFRow row = sheet.createRow((short)i); 
			
			//Date
			row.createCell((short)0).setCellValue(new HSSFRichTextString(dateFormat.format(value.getIkrValue().getCaptureTime())));
			
			//Value
			row.createCell((short)1).setCellValue(new HSSFRichTextString(value.getFormattedValue().getValue()));
			i++;
		}
	}
}
