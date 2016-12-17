package com.fsi.toolkits.jnlp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;

import com.fsi.fwk.apps.config.AbstractApplicationContext;
import com.fsi.toolkits.config.ToolkitContext;

public class JnlpClasspathBuilder {		
	private List<String> jars;	
	private BufferedWriter out;
	private StringBuffer classpathStr;
	private String classpathDir;
	private String classpathSep;
	private String protocol;
	private String host;
	private int port;
	private String filepath;
	
	private String jarDirReadProtocol;
	private String jarDirReadHost;
	private int jarDirReadPort;
	private String jarDirReadPath;
	
	private List<String> jarsExcluded;
	
	public JnlpClasspathBuilder() {}

	private void init() throws IOException {			
		JnlpClasspathGeneratorConfig conf = (JnlpClasspathGeneratorConfig)AbstractApplicationContext.getBean("jnlpClasspathGenerator");
		
		String distribHome = System.getProperty("ikr.home");		
		
		this.classpathDir = conf.getJarDirectoryWrite();
		File dir = new File(classpathDir);
		if (!dir.exists())
			dir.mkdir();
		
		classpathStr = new StringBuffer();
		jars = new ArrayList<String>();	
		jarsExcluded = conf.getJarsExcluded();
		
		String env = conf.getClasspathEnv();		
		String classpathFilename = distribHome + File.separator + "bin" + File.separator + "classpath" + File.separator ;
				
		if(isUnix()) {			
			classpathStr.append("CLASSPATH=");
			classpathFilename = classpathFilename + "monitor-server-classpath"+env+".sh";
			classpathSep = ":";
		}
		else {
			classpathSep = ";^\n";
			classpathStr.append("SET CLASSPATH=");
			classpathFilename = classpathFilename + "monitor-server-classpath"+env+".bat";
		}
		
		protocol = conf.getJnlpDownloadProtocol();
		host = conf.getJnlpDownloadHostname();
		port = Integer.parseInt(conf.getJnlpDownloadPort());
		filepath = conf.getJnlpDownloadPath();
		
		jarDirReadProtocol = conf.getJarDownloadProtocol();
		jarDirReadHost = conf.getJarDownloadHostname();
		jarDirReadPort = Integer.parseInt(conf.getJarDownloadPort());
		jarDirReadPath = conf.getJarDownloadPath();
		
		out = new BufferedWriter(new FileWriter(classpathFilename, false));			
	}
	
	public void process() {	
		try {			
			init();
			System.out.println("Parsing JNLP File ...");
			JnlpClasspath classpath = parse();		
			System.out.println("JNLP File Parsing DONE");			
			System.out.println("Building classpath ...");			
			
			for(JnlpJarModel model : classpath.getJars()) {
				String jarName = model.getHref();
				String jarClasspathName = jarName;
				int index = jarClasspathName.lastIndexOf("/");
				
				if(index>0) {
					jarClasspathName = jarClasspathName.substring(index+1);
				}				
				
				if (isJarRejected(jarName, jarsExcluded)) {
					System.out.println(jarName + " - REJECTED");
					continue;				
				}				
				
				classpathStr.append(classpathDir).append(File.separator).append(jarClasspathName).append(classpathSep);
				BufferedInputStream bis = null;
				BufferedOutputStream bos = null;
				try {
					// Read Jar File From Server
					URL url = null;
		        	if (jarDirReadPort == 0)				
						url = new URL(jarDirReadProtocol, jarDirReadHost, jarDirReadPath+"/"+jarName);				
					else
		        		url = new URL(jarDirReadProtocol, jarDirReadHost, jarDirReadPort, jarDirReadPath+"/"+jarName);
		        	URLConnection urlc = url.openConnection();
		        	bis = new BufferedInputStream(urlc.getInputStream());
		        	
		        	// Output - Write to Directory
		        	String filename = jarName;
		    		int lastIndex = filename.lastIndexOf("/");
		    		if(lastIndex>0) {
		    			filename = filename.substring(lastIndex+1);
		    		}
		        	bos = new BufferedOutputStream(new FileOutputStream(classpathDir+"/"+filename));
		        	int i=0;
		            while ((i = bis.read()) != -1)
		            {
		               bos.write( i );
		            }
		            
				} catch (MalformedURLException e) {
					System.err.println(e.getMessage());
				} catch (IOException e) {
					System.err.println(e.getMessage());
				} finally {
					if (bis != null) {
						try {
							bis.close();
						} catch (IOException e) {
							System.err.println(e.getMessage());
						}
					}
					
					if (bos != null) {
						try {
							bos.close();
						} catch (IOException e) {
							System.err.println(e.getMessage());
						}
					}
				}
			}
			
			try {
				out.write(classpathStr.toString());	
				System.out.println("Classpath building DONE");
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}			
			}
		} catch (IOException e1) {			
			e1.printStackTrace();
		}
	}
	
	private boolean isJarRejected(String jarName, List<String> filter) {
		if(filter==null || filter.size()==0)
			return true;
		if(jarName==null || jarName.length()==0)
			return false;
		String filterComponent;
		boolean rejected = false;
		int filterInd = 0;
		while(!rejected && filterInd<filter.size()){
			filterComponent = filter.get(filterInd);
			rejected = jarName.contains(filterComponent);
			filterInd++;
		}		
		return rejected;
	}
	
	private JnlpClasspath parse() {		
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate("jnlp", JnlpClasspath.class);		
		digester.addObjectCreate("jnlp/resources/jar", JnlpJarModel.class);	
			digester.addSetProperties("jnlp/resources/jar");
        digester.addSetNext("jnlp/resources/jar", "addJarModel"); 
        
        try {
        	URL url = null;
        	if (port == 0)
        		url = new URL(protocol, host, filepath); 
        	else
        		url = new URL(protocol, host, port, filepath);
			return (JnlpClasspath)digester.parse(url);
		} catch (Exception e) {		
			System.err.println("Impossible to parse JNLP File");
			e.printStackTrace();
		}		
		return null;
	}
	
	public void addJarModel(JnlpJarModel model) {
		String jarName = model.getHref();
		int lastIndex = jarName.lastIndexOf("/");
		if(lastIndex>0) {
			jarName = jarName.substring(lastIndex+1);
		}
		System.out.println(jarName);
		classpathStr.append(classpathDir).append(File.separator).append(jarName).append(classpathSep);
		if (!jarsExcluded.contains(jarName))
			jars.add(model.getHref());
	}	

	public static boolean isWindows(){		 
		String os = System.getProperty("os.name").toLowerCase();
		//windows
	    return (os.indexOf( "win" ) >= 0);  
	}
 
	public static boolean isUnix(){ 
		String os = System.getProperty("os.name").toLowerCase();
		//linux or unix
	    return (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0); 
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ToolkitContext.getContext().init("applicationContext-toolkits.xml", "toolkits");
			JnlpClasspathBuilder builder = new JnlpClasspathBuilder();
			builder.process();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.exit(0);
		}
	}
}
