package com.fsi.monitoring.connector.sysload;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.SysloadConnectorConfig;
import com.fsi.monitoring.connector.http.HttpConnectorImpl;

public class SysloadConnectorImpl
extends HttpConnectorImpl
implements SysloadConnector {
  
	private SysloadConnectorConfig sysloadConnectorConfig;
 
	private static final Logger logger = Logger.getLogger(SysloadConnectorImpl.class);
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private static final String service = "/services/sldmgts/sldapi/api_execute";	
	
	public SysloadConnectorImpl(SysloadConnectorConfig sysloadConnectorConfig) {
		super(sysloadConnectorConfig);
		this.sysloadConnectorConfig = sysloadConnectorConfig;
	}

	public Collection<SysloadData> getSysloadMetric(String metric,
													Date startDate,
													Date endDate)
	throws ConnectorException {
		checkStatus();
		
		Collection<SysloadData> res = new ArrayList<SysloadData>();
		
//        Date enddate = new Date();
//        Date date = new Date(enddate.getTime() - 5*60*1000);
	        
//        System.out.println(format.format(date));
//        System.out.println(format.format(enddate));
	
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("method", "getagthstdata"));
        qparams.add(new BasicNameValuePair("agent", sysloadConnectorConfig.getAgent()));
        qparams.add(new BasicNameValuePair("username", sysloadConnectorConfig.getUserName()));
        qparams.add(new BasicNameValuePair("password5", sysloadConnectorConfig.getPassword()));
        qparams.add(new BasicNameValuePair("showheader", "0"));
	        
        qparams.add(new BasicNameValuePair("metric", metric));
        qparams.add(new BasicNameValuePair("date", format.format(startDate)));
        qparams.add(new BasicNameValuePair("enddate", format.format(endDate)));
	        
        String responseBody = getBodyResponse(qparams, service);
	        
        logger.debug("Sysload responseBody: " + responseBody);

        try {
	        CSVReader reader = new CSVReader(new StringReader(responseBody), ';');
	        String [] nextLine;
	        while ((nextLine = reader.readNext()) != null) {
//	            System.out.println("--------");
//	            System.out.println("-Date: " + nextLine[3]);
//	            System.out.println("-Value: " + nextLine[4]);
	            
	            Date fetchDate = format.parse(nextLine[3]);
	            SysloadData data = new SysloadData(fetchDate, nextLine[4]);
	            
	            res.add(data);
	       }
		} catch (Exception exc) {
			String message = "Error while executing Sysload service: " + service + " agent=" + sysloadConnectorConfig.getAgent();
			Exception exception = new Exception(message, exc);
			reportFailure(exception);
		}
		
		return res;
	}
	
	public class SysloadData {
		private Date date;
		private String value;
		
		public SysloadData(Date date,
						   String value) {
			this.date = date;
			this.value = value;
		}
		
		public Date getDate() {
			return date;
		}
		
		public String getValue() {
			return value;
		}
	}
}
