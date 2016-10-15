package com.fsi.monitoring.report;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;


import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



import com.fsi.monitoring.report.tree.TreeController;
import com.fsi.monitoring.sec.SecurityBean;
import com.fsi.fwk.util.DateUtil;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;


public class ReportGenerationBean 
extends AccessControlBean {

	private static final String PANEL_STACK = "ACTIVATED";
	
	private TreeController treeController;
	private String panelStack = null;
	
	
	private ReportType reportType;
	private ReportConfig reportConfig;
	private Date fromDate;
	private Date toDate;
	
	private Effect valueChangeEffect2;

	private static final long serialVersionUID = -3102239630083380598L;


	public void init(ActionEvent action) {
		if (isAuthorized(60,"reportGeneration")) {
			treeController = new TreeController();
			treeController.init();
			
	        valueChangeEffect2 = new Highlight("#fda505");
	        valueChangeEffect2.setFired(true);
		}
	}
	
	public void setReportConfig(ReportConfig reportConfig) {
		this.reportConfig = reportConfig;
	}
	
	public ReportConfig getReportConfig() {
		return reportConfig;
	}
	
	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}
	
	public ReportType getReportType() {
		return reportType;
	}
	
	public TreeController getTree() {
		return treeController;
	}
	
	public void generate(ActionEvent action) {
		if (!isAuthorized(31,"reportGeneration")) {
			return;
		}
		
		try {
			ReportConfigurationBean config = (ReportConfigurationBean)FacesUtils.getManagedBean("reportConfigurationBean");
			
			String templateFile = reportType.getName().replaceAll("\\s", "_")
			  					  + "-"
			  					  + reportConfig.getName().replaceAll("\\s", "_");
			
			
			File in = new File(config.getActionFolder() + templateFile + ".xml");
	     
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			
			Document doc = parser.parse(in);
			
			NodeList schedulerFileActionNodes = doc.getElementsByTagName("schedulerFileAction");
			Node schedulerFileActionNode = schedulerFileActionNodes.item(0);
			NamedNodeMap attr = schedulerFileActionNode.getAttributes();
			Node sourceNode = attr.getNamedItem("source");
			
			String fullTemplateFile = config.getTemplateFolder() 
							  		  + templateFile
							  		  + ".iok";			
			sourceNode.setNodeValue(fullTemplateFile);
			
			NodeList jdbcNodes = doc.getElementsByTagName("jdbc");
			Node sqlChild = jdbcNodes.item(0);
			attr = sqlChild.getAttributes();
			Node sqlRetrieveNode = attr.getNamedItem("sqlRetrieve");
			String sqlRetrieve = sqlRetrieveNode.getNodeValue();	
			String sqlRetrieve1 = sqlRetrieve.replaceFirst("fromDate", DateUtil.getDate(fromDate));
			String sqlRetrieve2 = sqlRetrieve1.replaceFirst("toDate", DateUtil.getDate(toDate));
			sqlRetrieveNode.setNodeValue(sqlRetrieve2);
			 	
			String now = DateUtil.getDate(new Date(),"yyyy_MM_dd_HH_mm_ss_S");
			
			NodeList saveNodes = doc.getElementsByTagName("schedulerFileActionSaveIok");
			Node saveNode = saveNodes.item(0);
			attr = saveNode.getAttributes();
			Node saveLocationNode = attr.getNamedItem("location");			 
			String outLocation = config.getOutFolder()
							  + templateFile
							  + "_"
							  + now
							  + ".iok";
			saveLocationNode.setNodeValue(outLocation);
			
			NodeList mailNodes = doc.getElementsByTagName("schedulerSendEmailAction");
			Node mailNode = mailNodes.item(0);
			attr = mailNode.getAttributes();
			Node attachNode = attr.getNamedItem("attachment");
			attachNode.setNodeValue(outLocation);
			
			Node toNode = attr.getNamedItem("to");
			SecurityBean securityBean = (SecurityBean)FacesUtils.getManagedBean("securityBean");
			String email = securityBean.getUser().getEmail();
			toNode.setNodeValue(email);
			

			Source src = new DOMSource(doc);
			String outputAction = config.getWatchFolder()
								  + "action"
								  + now
								  + ".xml";
	        File file = new File(outputAction);
	        Result result = new StreamResult(file);

	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.transform(src, result);
	         
		} catch (Exception exc) {
			System.out.println(exc);
		}
		panelStack = "GENERATED";
	}

	public String visokioAction() {
		return "visokioReportGeneration";
	}
	
	public String alertAction() {
		return "alertReportGeneration";
	}
	
	public String dataAction() {
		return "ikrWorkSpace";
	}
	
	public String schedulerAction() {
		return "schedulerGeneration";
	}
	
	
	public void setActivateStack() {
		panelStack = PANEL_STACK;
	}
	
	public String getPanelStack() {
		return panelStack;
	}
	
	public Date getFromDate() {
		return fromDate;
	}
	
	public void setFromDate(Date date) {
		this.fromDate = date;
	}
	
	public Date getToDate() {
		return toDate;
	}
	
	public void setToDate(Date date) {
		this.toDate = date;
	}	
	
    public TimeZone getTimeZone() {
        return java.util.TimeZone.getDefault();
    }	

    public void effect2ChangeListener(ValueChangeEvent event){
        //System.out.println("Value change fired "+ event.getOldValue() + " : " + event.getNewValue());
        valueChangeEffect2.setFired(false);
    }    
}
