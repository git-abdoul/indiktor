package com.fsi.monitoring.kpi.monitor.network.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class PingService implements Serializable {

	private static final Logger LOG = Logger.getLogger(PingService.class);

	private static final long serialVersionUID = -6295696308102913265L;

	public class PingResult implements Serializable {

		private final String OS_NAME = System.getProperty("os.name").toLowerCase();
		private static final long serialVersionUID = -936971979017852640L;
		protected String hostname = null;
		protected int count = 0;
		protected int byteSize = 0;
		protected double delays [] = null;

		public String getCommand() {
			String command = null;
			if (OS_NAME.indexOf("win") >= 0)
				command = "ping -l " + byteSize + " -n " + count + " " + hostname;
			else
				command = "/usr/sbin/ping -s " + hostname + " " + byteSize + " " + count;
			return command;
		}

		public String getHostname() {
			return hostname;
		}
		public int getCount() {
			return count;
		}
		public int getByteSize() {
			return byteSize;
		}
		public double[] getDelays() {
			return delays;
		}

		public double getMin() {
			double min = 1000000;
			if (delays == null)
				return 0;
			for (int i = 0; i < delays.length; ++i)
				min = Math.min(min, delays[i]);
			return min;
		}

		public double getMax() {
			double max = 0;
			if (delays == null)
				return 0;
			for (int i = 0; i < delays.length; ++i)
				max = Math.max(max, delays[i]);
			return max;
		}

		public double getAverage() {
			double sum = 0;
			if (delays == null)
				return 0;
			for (int i = 0; i < delays.length; ++i)
				sum += delays[i];
			sum /= delays.length;
			return sum;
		}

		public double getPercent() {
			return ((double)((delays != null) ? delays.length : 0)) / count;
		}

		public void readResult(InputStream in) {
			ArrayList<Double> results = new ArrayList<Double>();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			try {
				String line = null;

				while ((line = br.readLine()) != null) {
					if (line.indexOf("from") < 0)
						continue;
					int indexStart = line.indexOf("time");
					int indexStop = line.indexOf("ms");
					if (indexStop < 0 || indexStart < 0)
						continue;

					String val = line.substring(indexStart+5, indexStop);
					Double delay = Double.valueOf(val.trim());
					if (delay != null)
						results.add(delay);
				}

				if (results.size() > 0) {
					delays = new double[results.size()];
					for (int i = 0; i < results.size(); ++i)
						delays[i] = ((Double)results.get(i)).doubleValue();
				}
			} catch(Throwable e) {
				LOG.error("Ping reading failed for " + getCommand(), e);
			}
		}
	}
	
	public class TraceResult implements Serializable {

		private static final long serialVersionUID = -6131281419010871338L;
		private final String OS_NAME = System.getProperty("os.name").toLowerCase();
		protected String hostname = null;
		protected String output = null;

		public String getHostname() {
			return hostname;
		}
		public String getOutput() {
			return output;
		}

		public String getCommand() {
			String command = null;
			if (OS_NAME.indexOf("win") >= 0)
				command = "tracert  " + hostname;
			else
				command = "/usr/sbin/traceroute " + hostname;
			return command;
		}

		public void readResult(InputStream in) {
			StringBuffer buf = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			try {
				String line = null;
				while ((line = br.readLine()) != null) {
					buf.append(line);
					buf.append("\n");
				}

				output = buf.toString();
			} catch(Throwable e) {
				LOG.error("Trace reading failed for " + hostname, e);
			}
		}
	}
	
	public List<PingResult> ping(String[] hostnames, int count, int byteSize) {
		List<PingResult> results = new ArrayList<PingResult>();
		for(String hostname : hostnames) {
			PingResult ping = ping(hostname, count, byteSize);
			results.add(ping);
		}		
		return results;
	}

	public PingResult ping(String hostname, int count, int byteSize) {
		if (hostname == null || count <= 0 || byteSize <= 0 || byteSize > 65536)
			return null;

		PingResult pres = this.new PingResult();
		pres.count = count;
		pres.byteSize = byteSize;
		pres.hostname = hostname;

		Process p = null;
		String command = pres.getCommand();
		try {
			p = Runtime.getRuntime().exec(command);
			pres.readResult(p.getInputStream());
		}
		catch (Exception e) {
			LOG.error("Ping failed for " + command, e);
		}
		finally {
			if (p != null)
				p.destroy();
		}

		return pres;
	}

	public TraceResult traceroute(String hostname) {
		if (hostname == null)
			return null;

		TraceResult pres = this.new TraceResult();
		pres.hostname = hostname;

		Process p = null;
		String command = pres.getCommand();
		try {
			p = Runtime.getRuntime().exec(command);
			pres.readResult(p.getInputStream());
		}
		catch (Exception e) {
			LOG.error("Traceroute failed for " + command, e);
		}
		finally {
			if (p != null)
				p.destroy();
		}
		return pres;
	}
	
	public String executeCmd(String command) {

		if (command == null)
			return null;

		String output = null;
		StringBuffer buf = new StringBuffer();

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			try {
				String line = null;
				while ((line = br.readLine()) != null) {
					buf.append(line);
					buf.append("\n");
				}

				output = buf.toString();
			} catch(Throwable e) {
				LOG.error("Command reading failed for " + command, e);
			}
		}
		catch (Exception e) {
			LOG.error("Traceroute failed for " + command, e);
		}
		finally {
			if (p != null)
				p.destroy();
		}

		return output;
	}
}
