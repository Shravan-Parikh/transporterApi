package com.springboot.EmailSender.Service;

import com.springboot.EmailSender.Entities.EmailMessage;
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
	
	// Sender's email that will be directly taken from env file
	 @Value("${email_name}")
	 private String senderEmail;

    public EmailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Transactional(rollbackFor = Exception.class)
	@Override
    public emailSentStatus sendEmail(EmailMessage emailMessage) {
		String receiverMailId = emailMessage.getReceiverMailId();
		String senderName = emailMessage.getSenderName();
		String companyId = emailMessage.getCompanyId();

		if (emailMessage.getRole() == null){
			emailMessage.setRole(EmailMessage.roles.VIEWER);
		}

	    try {
	    	 SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
	         simpleMailMessage.setFrom(senderEmail);
	         simpleMailMessage.setTo(receiverMailId); // requested from the user
	         String Subject = senderName + " has invited you to log in to 'Liveasy'";
	         simpleMailMessage.setSubject(Subject);
	         String body = "Welcome to Liveasy, kindly login through the following url :- https://shipperwebapp.web.app/#/?companyId="+companyId+"&role="+ emailMessage.getRole();
	         simpleMailMessage.setText(body);
	      
		     // mail sending function
	         this.mailSender.send(simpleMailMessage);

	         // status will be saved as SENT if the mail is sent successfully
	        return emailSentStatus.SENT; 
	    } catch (Exception e) {
	        // If sending of the email fails then the status is saved as UNSENT
	        return emailSentStatus.NOT_SENT;
	    }
	}

	
}
