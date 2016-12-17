package com.fsi.publisher.email;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.sun.mail.smtp.SMTPTransport;


public class EmailPublisher {
	private static final Logger logger = Logger.getLogger(EmailPublisher.class);	
	
	private MailSender sender;
	private String from;
	private boolean smtpAuthRequired;
	private boolean useSendmail;
	private boolean smtpSecurityRequired;

	public void setSender(MailSender sender) {
		this.sender = sender;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public void setUseSendmail(String useSendmail) {
		if (useSendmail!=null && useSendmail.length()>0)
			this.useSendmail = Boolean.valueOf(useSendmail);
	}
	
	public void setSmtpAuthRequired(String smtpAuthRequired) {
		if (smtpAuthRequired!=null && smtpAuthRequired.length()>0)
			this.smtpAuthRequired = Boolean.valueOf(smtpAuthRequired);
	}
	
	public void setSmtpSecurityRequired(String smtpSecurityRequired) {
		if (smtpSecurityRequired!=null && smtpSecurityRequired.length()>0)
			this.smtpSecurityRequired = Boolean.valueOf(smtpSecurityRequired);
	}

	public void publish(EmailMessage message) {
		if (useSendmail)
			sendEmailUsingSendmail(message);
		else
			sendEmailUsingApi(message);
	}
	
	private void sendEmailUsingApi(EmailMessage message) {
		try {
			logger.debug("Sending Email using API");
			String protocol = (smtpSecurityRequired)?"smtps":"smtp";
			JavaMailSenderImpl mailSender = (JavaMailSenderImpl)sender;
			Properties props = System.getProperties();
			props.put("mail."+ protocol +".host", mailSender.getHost());		
			props.put("mail."+ protocol +".auth", smtpAuthRequired);
			
			Session session = Session.getInstance(props, null);
			if (logger.isDebugEnabled())
				session.setDebug(true);
			
			Message msg = new MimeMessage(session);
			if (from != null)
				msg.setFrom(new InternetAddress(from));
			else
				msg.setFrom();
			 
			StringBuffer to = new StringBuffer();
			int i = 1;
			int len = message.getRecipients().length;
			String[] recipients = message.getRecipients();
			for(String adress : recipients) {
				to.append(adress);
				if (i < len)
					to.append(",");
				i = i + 1;
			}
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to.toString(), false));
			msg.setSubject(message.getSubject());
			msg.setText(message.getContent());			
			
			msg.setHeader("X-Mailer", "IndiKtor");
			msg.setSentDate(new Date());
			
		
			SMTPTransport transport = (SMTPTransport)session.getTransport(protocol);
			try {
				if (smtpAuthRequired)
					transport.connect(mailSender.getHost(), mailSender.getUsername(), mailSender.getPassword());
				else
					transport.connect();
				transport.sendMessage(msg, msg.getAllRecipients());
		    } finally {
		    	transport.close();
			}
		    
			if (logger.isDebugEnabled()) {			
				logger.debug("Email sent to recipicients : " + to.toString());
			}
			
		} catch (Exception e) {
			logger.info("Error Occured while trying to send an Email : " + e.getMessage(), e);
		}
	}
	
	private void sendEmailUsingSendmail(EmailMessage message) {
		logger.debug("Sending Email using sendmail command");
		StringBuffer cmdBuffer = new StringBuffer("sendmail");
		cmdBuffer.append("-f " + from);
		cmdBuffer.append("-t ");
		int i = 1;
		int len = message.getRecipients().length;
		for(String addr : message.getRecipients()) {
			cmdBuffer.append(addr);
			if (i < len)
				cmdBuffer.append(";");
			i = i + 1;
		}
		
//		String subj = ("Subject:" + message.getSubject());
		String content = message.getContent();		
//		String command = "echo '" + subj + "\n" + content + "' | " + cmdBuffer.toString();
		String command = "echo '" + content + "' | " + cmdBuffer.toString();
		logger.debug(command);
		final boolean failOnError = true;
		
		try {
		      Runtime runtime = Runtime.getRuntime();
		      final Process process = runtime.exec(command);
		      // Standard output consumption of application third-party in separate thread.
		      new Thread() {
		        @Override
		        public void run() {
		          try {
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            String line = "";
		            try {
		              while ((line = reader.readLine()) != null) {
		            	logger.info(line);
		                if (failOnError) {
		                  if (line.contains("ERROR")) {
		                    process.destroy();
		                  }
		                }
		              }
		            } finally {
		              reader.close();
		            }
		          } catch (IOException ioe) {
		        	  logger.error(ioe.getMessage(), ioe);
		          }
		        }
		      }.start();

		      // Error output consumption of application third-party in separate thread.
		      new Thread() {
		        @Override
		        public void run() {
		          try {
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		            String line = "";
		            try {
		              while ((line = reader.readLine()) != null) {
		            	logger.error(line);
		                if (failOnError) {
		                  if (line.contains("ERROR")) {
		                    process.destroy();
		                  }
		                }
		              }
		            } finally {
		              reader.close();
		            }
		          } catch (IOException ioe) {
		        	  logger.error(ioe.getMessage(), ioe);
		          }
		        }
		      }.start();

		      try {
		        process.waitFor();
		      } catch (InterruptedException e) {
		    	  logger.error(e);
		      }
		    } catch (IOException e) {
		      logger.error(e);
		    }
	}
}
