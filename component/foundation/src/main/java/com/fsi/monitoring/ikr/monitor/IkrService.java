package com.fsi.monitoring.ikr.monitor;


public class IkrService {
	private int id;
	private String serviceName;
	private String classname;
	private String extension;
	
	public IkrService(int id, String serviceName, String classname,
			String extension) {
		super();
		this.id = id;
		this.serviceName = serviceName;
		this.classname = classname;
		this.extension = extension;
	}	

	public IkrService() {
		super();
		this.serviceName = "";
		this.classname = "";
		this.extension = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}	
}
