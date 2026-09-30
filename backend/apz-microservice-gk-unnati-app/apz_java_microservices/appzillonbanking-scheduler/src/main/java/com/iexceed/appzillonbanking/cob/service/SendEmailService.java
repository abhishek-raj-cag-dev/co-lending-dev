package com.iexceed.appzillonbanking.cob.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.payload.SmsAndEmailDtls;

public class SendEmailService {

	private static final Logger logger = LogManager.getLogger(SendEmailService.class);

	private SendEmailService() {

	}

	public static JSONObject sendMail(SmsAndEmailDtls smsAndEmailDtls, Properties prop) {
		logger.debug(" Send EMAIL service :: Service Started ");
		JSONObject lResponse = new JSONObject();
		String toEmail = smsAndEmailDtls.getEmailId();
		String from = prop.getProperty(Constants.FROM_EMAIL);
		String host = prop.getProperty(Constants.EMAIL_HOST);
		String port = prop.getProperty(Constants.EMAIL_PORT);
		String emailSubject = smsAndEmailDtls.getEmailTitle();
		String timeout = prop.getProperty(Constants.EMAIL_TIMEOUT);
		String emailBody = smsAndEmailDtls.getEmailBody();

		try {
			if (StringUtils.isNotEmpty(toEmail) && StringUtils.isNotEmpty(emailSubject)
					&& StringUtils.isNotEmpty(emailBody)) {
				logger.debug("SendMailDetails -- From :: " + from + " -- To :: " + toEmail);
				logger.debug("SendMailDetails -- subject :: " + emailSubject);
				Properties props = new Properties();
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.auth", "false");
				props.put("mail.smtp.ssl.trust", host);
				props.put("mail.smtp.host", host);
				props.put("mail.smtp.port", port);
				props.put("mail.debug", "true");
				props.put("mail.smtp.timeout", timeout);
				props.put("mail.smtp.connectiontimeout", timeout);

				Session session = Session.getInstance(props);

				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				String[] recipientList = toEmail.split(",");
				InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
				int counter = 0;
				for (String recipient : recipientList) {
					recipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}
				message.setRecipients(Message.RecipientType.TO, recipientAddress);
				message.setSubject(emailSubject);
				if (smsAndEmailDtls.isHasAttachment()) {
					if (smsAndEmailDtls.getAttachmentType().equals("pdf")) {
						Multipart multipart = new MimeMultipart();

						MimeBodyPart attachmentBodyPart = new MimeBodyPart();
						DataSource dataSrc;
						try (InputStream in = MimeUtility.decode(
								new ByteArrayInputStream(smsAndEmailDtls.getAttachmentContent().getBytes("UTF-8")),
								"base64")) {
							dataSrc = new ByteArrayDataSource(in, "application/pdf");
						}
						attachmentBodyPart.setDataHandler(new DataHandler(dataSrc));
						attachmentBodyPart.setFileName("AOF form.pdf");
						multipart.addBodyPart(attachmentBodyPart);

						BodyPart htmlBodyPart = new MimeBodyPart();
						htmlBodyPart.setContent(emailBody, "text/html");
						multipart.addBodyPart(htmlBodyPart);
						message.setContent(multipart);
					} else if (smsAndEmailDtls.getAttachmentType().equals("zip")) {
						Multipart multipart = new MimeMultipart();

						MimeBodyPart attachmentBodyPart = new MimeBodyPart();
						DataSource dataSrc;
						try (InputStream in = MimeUtility.decode(
								new ByteArrayInputStream(smsAndEmailDtls.getAttachmentContent().getBytes("UTF-8")),
								"base64")) {
							dataSrc = new ByteArrayDataSource(in, "application/zip");
						}
						attachmentBodyPart.setDataHandler(new DataHandler(dataSrc));
						attachmentBodyPart.setFileName("Document.zip");
						multipart.addBodyPart(attachmentBodyPart);

						BodyPart htmlBodyPart = new MimeBodyPart();
						htmlBodyPart.setContent(emailBody, "text/html");
						multipart.addBodyPart(htmlBodyPart);
						message.setContent(multipart);
					}
				} else {
					message.setContent(emailBody, "text/html");
				}
				Transport.send(message);

				logger.debug("Email sent successfully");
				lResponse.put("status", "success");
				lResponse.put("msg", "Email sent successfully.");

			} else {
				logger.error("Required value missing in request.");
				lResponse.put("status", "failure");
				lResponse.put("errMsg", "Required value missing in request.");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			lResponse.put("status", "failure");
			lResponse.put("errMsg", e.getMessage());
		}
		logger.debug(" Send EMAIL service :: Service end ");
		logger.debug("Response from send EMAIL service :: " + lResponse);
		return lResponse;
	}

}
