package com.fsi.monitoring.snmp;

public class SnmpConfig {
	private long id;
	private String name;
	private int version;
	private String hostname;
	private int port;
	private int genericTrapType;
	private int specificTrapType;
	private String community;
	private String user;
	private String authProtocol;
	private String authPassword;
	private String privProtocol;
	private String privPassword;
	private String contextName;
	private String engineID;
	
	public SnmpConfig(){
		version = 1;
		community = "public";
	}
	
	public SnmpConfig(long id, String name, int version, String hostname,
			int port, int genericTrapType, int specificTrapType, String community, String user, String authProtocol,
			String authPassword, String privProtocol, String privPassword,
			String contextName, String engineID) {
		super();
		this.id = id;
		this.name = name;
		this.version = version;
		this.hostname = hostname;
		this.port = port;
		this.genericTrapType = genericTrapType;
		this.specificTrapType = specificTrapType;
		this.community = community;
		this.user = user;
		this.authProtocol = authProtocol;
		this.authPassword = authPassword;
		this.privProtocol = privProtocol;
		this.privPassword = privPassword;
		this.contextName = contextName;
		this.engineID = engineID;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getAuthProtocol() {
		return authProtocol;
	}
	public void setAuthProtocol(String authProtocol) {
		this.authProtocol = authProtocol;
	}
	public String getAuthPassword() {
		return authPassword;
	}
	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}
	public String getPrivProtocol() {
		return privProtocol;
	}
	public void setPrivProtocol(String privProtocol) {
		this.privProtocol = privProtocol;
	}
	public String getPrivPassword() {
		return privPassword;
	}
	public void setPrivPassword(String privPassword) {
		this.privPassword = privPassword;
	}
	public String getContextName() {
		return contextName;
	}
	public void setContextName(String contextName) {
		this.contextName = contextName;
	}
	public String getEngineID() {
		return engineID;
	}
	public void setEngineID(String engineID) {
		this.engineID = engineID;
	}

	public int getGenericTrapType() {
		return genericTrapType;
	}

	public void setGenericTrapType(int genericTrapType) {
		this.genericTrapType = genericTrapType;
	}

	public int getSpecificTrapType() {
		return specificTrapType;
	}

	public void setSpecificTrapType(int specificTrapType) {
		this.specificTrapType = specificTrapType;
	}
}
