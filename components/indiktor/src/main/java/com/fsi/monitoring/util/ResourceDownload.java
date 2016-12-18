package com.fsi.monitoring.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import javax.faces.context.ExternalContext;

import com.icesoft.faces.context.Resource;

public class ResourceDownload implements Resource, Serializable {
	private static final long serialVersionUID = 1594739381049233494L;
	
	public static final String PDF = "pdf";
	public static final String EXCEL = "vnd.ms-excel";
	
	private String type;
	private String resourceName;
	private String resourcePath;
    private InputStream inputStream;
    private final Date lastModified;
    private ExternalContext extContext;
    private boolean finished;

    public ResourceDownload(ExternalContext ec, String resourcePath, String resourceName, String type) {
        this.extContext = ec;
        this.resourceName = resourceName;
        this.resourcePath = resourcePath;
        this.lastModified = new Date();      
        this.type = type;
    }
    
    public InputStream open() throws IOException {
    	finished = true;
        if (inputStream == null && resourceName != null && resourceName.length()>0) {
        	InputStream stream = null;
        	if(extContext != null)
        		stream = extContext.getResourceAsStream(resourcePath + "/" + resourceName);
        	else {
        		stream = new FileInputStream(new File(resourcePath + "/" + resourceName));
        	}
            byte[] byteArray = toByteArray(stream);
            inputStream = new ByteArrayInputStream(byteArray);
        }        
        return inputStream;
    }
    
    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int len = 0;
        while ((len = input.read(buf)) > -1) output.write(buf, 0, len);
        return output.toByteArray();
    }
    
    public String getResourceName() {
    	return resourceName;
    }
    
    public String calculateDigest() {
        return resourceName;
    }

    public Date lastModified() {
        return lastModified;
    }

    public void withOptions(Options arg0) throws IOException {
    }

	public boolean isFinished() {
		return finished;
	}

	public String getType() {
		return type;
	} 

}
