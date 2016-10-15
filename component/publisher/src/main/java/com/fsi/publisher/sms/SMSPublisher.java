package com.fsi.publisher.sms;


import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.fsi.monitoring.user.User;
import com.fsi.monitoring.user.UserPM;

public class SMSPublisher {
	private static final Logger logger = Logger.getLogger(SMSPublisher.class);
	
	public static final int MAX_SMS_SIZE = 160;
	
	private boolean isSmtpEnable = false;
	private boolean isSmtpSecurityEnable = false;
	private String smtpUser;
	private String smtpPassword;
	private String smtpHost;
	private String smtpFrom;
	private UserPM userPM = null;
	
	private Properties properties;

	public void publish(SMSMessage message) {		
		String[] recipients = message.getRecipients();
		String content = message.getContent();
		
		sendSMS(recipients, content);
	}

	public void setSmtpEnable(boolean isSmtpEnable) {
		this.isSmtpEnable = isSmtpEnable;
	}

	public void setSmtpSecurityEnable(boolean isSmtpSecurityEnable) {
		this.isSmtpSecurityEnable = isSmtpSecurityEnable;
	}

	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}
	
	public void setSmtpFrom(String smtpFrom) {
		this.smtpFrom = smtpFrom;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
		properties = System.getProperties();
		properties.put("mail.smtp.host", smtpHost);
	}	

	/**
	 * SEND A SMS MODULE Split the message body in shorter messages if the
	 * original one contains more than MAX_SMS_SIZE characters If the maximum
	 * number of message is reached, the notification invites the user to
	 * consult his email box. Then calling sendSingleSMS
	 * 
	 * @param body
	 * @param recipients
	 *            phones number
	 * @return the number of messages sent. (-1 if the user was invited to
	 *         consult his inbox)
	 */
	private int sendSMS(String[] recipients, String content) {
		int msgCountTotal;
		int msgIndex;
		int startCharPos;
		int endCharPos;
		String partialBody;

		if (content.length() > MAX_SMS_SIZE) {
			msgCountTotal = (content.length() / (MAX_SMS_SIZE - 6)) + 1;

			msgIndex = 1;
			startCharPos = 0;
			endCharPos = MAX_SMS_SIZE - 7;
			while (msgIndex <= msgCountTotal) {
				partialBody = content.substring(startCharPos, endCharPos);
				// send sms
				sendSingleSMS(recipients, partialBody + " [" + msgIndex + "/" + msgCountTotal + "]");
				// sendSingleSMS(partialBody+" ["+msgIndex+"/"+msgCountTotal+"]");
				startCharPos = endCharPos + 1;
				endCharPos = Math.min(content.length(), endCharPos	+ MAX_SMS_SIZE - 6);
				msgIndex++;
			}
			return msgCountTotal;
		} else {
			sendSingleSMS(recipients, content);
			return 1;
		}
	}

	/**
	 * Send SMS to Gateways only when the gateways are activated in the
	 * configuration file (i.e sms.smtpgateway.send)
	 * 
	 * @param body
	 * @param recipients
	 *            phones number
	 * @param textBody
	 *            with less than 160 characters
	 */

	public void sendSingleSMS(String[] recipients, String body) {
		if (isSmtpEnable) 
			sendSingleSMSThroughSMTPGateway(recipients, body);
	}

	/**
	 * send a single SMS through SMS gateway with SMTP protocol
	 * 
	 * @param body
	 */
	private void sendSingleSMSThroughSMTPGateway(String[] recipients, String body) {
		try {			
			Session session = null;
			if (isSmtpSecurityEnable) {
				session = Session.getDefaultInstance(properties,
						new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(smtpUser, smtpPassword);
							}
						});
			} else {
				session = Session.getDefaultInstance(properties, null);
			}
			// Create a new message --
			Message msg = new MimeMessage(session);
			// Set the FROM and TO fields --
			msg.setFrom(new InternetAddress(smtpFrom));
			for (String recv : recipients) {
				msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recv, false));
			}
			msg.setText(body);
			// Set some other header information --
			msg.setSentDate(new Date());
			// Send the message --
			Transport.send(msg);
		} catch (Exception ex) {
			logger.error("Impossible to send a single sms", ex);
		}
	}

}

