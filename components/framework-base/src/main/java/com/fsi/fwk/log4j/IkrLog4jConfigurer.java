package com.fsi.fwk.log4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.util.Log4jConfigurer;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

public class IkrLog4jConfigurer extends Log4jConfigurer {

	public IkrLog4jConfigurer() {}
	
	public static void init(String ikrPropertiesEnvFile, String ikrComponentName) throws IOException{
		String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(ikrPropertiesEnvFile);
		URL url = ResourceUtils.getURL(resolvedLocation);
		
		Properties envProperties = new Properties();
		String path = url.getFile();
		envProperties.load(new FileInputStream(path));
		
		PropertyConfigurator.configure(getLogProperties(ikrComponentName, envProperties));
	}
	
	private static Properties getLogProperties(String ikrComponentName, Properties envProperties) {
		Properties logProperties = new Properties();
		
		logProperties.setProperty("log4j.rootLogger", envProperties.getProperty("ikr.log.globalLevel")+ ", rollingFile");
		
		logProperties.setProperty("log4j.appender.rollingFile", "org.apache.log4j.RollingFileAppender");
		logProperties.setProperty("log4j.appender.rollingFile.File", envProperties.getProperty(ikrComponentName + ".log.filePath"));
		logProperties.setProperty("log4j.appender.rollingFile.MaxFileSize", envProperties.getProperty(ikrComponentName + ".log.fileMaxSize"));
		logProperties.setProperty("log4j.appender.rollingFile.layout", "org.apache.log4j.PatternLayout");
		logProperties.setProperty("log4j.appender.rollingFile.layout.ConversionPattern", "%d{dd MMM yyyy HH:mm:ss} %-5p [%t] %c%x - %m%n");
		logProperties.setProperty("log4j.appender.rollingFile.MaxBackupIndex", envProperties.getProperty(ikrComponentName + ".log.maxBackupFile"));
		
		logProperties.setProperty("log4j.logger.com.fsi", envProperties.getProperty(ikrComponentName + ".log.level"));
		logProperties.setProperty("log4j.logger.net.sf.ehcache", envProperties.getProperty("ehcache.log.level"));
		logProperties.setProperty("log4j.logger.org.springframework", envProperties.getProperty("spring.log.level"));
		
		return logProperties;
	}

}
