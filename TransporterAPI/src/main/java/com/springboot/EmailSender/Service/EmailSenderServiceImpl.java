package com.springboot.EmailSender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.util.Value;
import com.springboot.EmailSender.Dao.EmailSenderDao;
import com.springboot.EmailSender.Entities.EmailMessage.emailSentStatus;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {
	
	@Autowired
	EmailSenderDao emailSenderDao;


    private final JavaMailSender mailSender;

	@Value("${email_name}")
	 private String senderEmail;

    public EmailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Transactional(rollbackFor = Exception.class)
	@Override
    public emailSentStatus sendEmail(String receiverMailId, String senderName) {
	    try {
	    	 SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
	         simpleMailMessage.setFrom(senderEmail);
	         simpleMailMessage.setTo(receiverMailId);
	         String Subject = senderName + " has invited you to view file 'Liveasy'";
	         simpleMailMessage.setSubject(Subject);
	         String body = "Welcome to Liveasy, kindly login through the following url :- https://shipperwebapp.web.app/#/";
	         simpleMailMessage.setText(body);
	      

	         this.mailSender.send(simpleMailMessage);
	        return emailSentStatus.SENT;
	    } catch (Exception e) {
	        // If sending the email failed
	        return emailSentStatus.NOT_SENT;
	    }
	}

	
}