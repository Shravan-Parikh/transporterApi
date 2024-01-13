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

import java.util.Optional;
import java.util.UUID;

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
		String companyName = emailMessage.getCompanyName();

		String inviteId ="inviteId:"+ UUID.randomUUID().toString();
		emailMessage.setInviteId(inviteId);

	    try {
	    	 SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
	         simpleMailMessage.setFrom(senderEmail);
	         simpleMailMessage.setTo(receiverMailId); // requested from the user
	         String Subject = companyName + " has invited you to log in to 'Liveasy'";
	         simpleMailMessage.setSubject(Subject);
	         String body = "Welcome to Liveasy, kindly login through the following url :- https://shipperwebapp.web.app/?inviteId="+inviteId;
	         simpleMailMessage.setText(body);
	      
		     // mail sending function
	         this.mailSender.send(simpleMailMessage);
			 emailSenderDao.save(emailMessage);

	         // status will be saved as SENT if the mail is sent successfully
	        return emailSentStatus.SENT; 
	    } catch (Exception e) {
	        // If sending of the email fails then the status is saved as UNSENT
	        return emailSentStatus.NOT_SENT;
	    }
	}

	@Override
	public EmailMessage getInviteDetails(String inviteId){
		Optional<EmailMessage> emailMessage = emailSenderDao.findById(inviteId);
		return emailMessage.orElse(null);
	}

	@Override
	public String deleteInviteDetails(String inviteId){
		try{
			emailSenderDao.deleteById(inviteId);
			return "Deleted Successfully";
		}
		catch(Exception e){
			return "InviteId not Found!";
		}
	}
}
