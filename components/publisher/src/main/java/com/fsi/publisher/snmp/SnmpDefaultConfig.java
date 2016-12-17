package com.fsi.publisher.snmp;

public class SnmpDefaultConfig {
	private String MIBFile;
	private int timeout;
	private int retries;
	private int maxRepetition;
	private int nonRepeaters;
	
	public String getMIBFile() {
		return MIBFile;
	}
	public void setMIBFile(String file) {
		MIBFile = file;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public int getRetries() {
		return retries;
	}
	public void setRetries(int retries) {
		this.retries = retries;
	}
	public int getMaxRepetition() {
		return maxRepetition;
	}
	public void setMaxRepetition(int maxRepetition) {
		this.maxRepetition = maxRepetition;
	}
	public int getNonRepeaters() {
		return nonRepeaters;
	}
	public void setNonRepeaters(int nonRepeaters) {
		this.nonRepeaters = nonRepeaters;
	}
	
}
