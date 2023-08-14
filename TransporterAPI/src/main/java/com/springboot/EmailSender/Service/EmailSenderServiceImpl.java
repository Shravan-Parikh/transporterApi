package com.springboot.EmailSender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.EmailSender.Dao.EmailSenderDao;


@Service
public class EmailSenderServiceImpl implements EmailSenderService {
	
	@Autowired
	EmailSenderDao emailSenderDao;


    private final JavaMailSender mailSender;

    public EmailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Transactional(rollbackFor = Exception.class)
	@Override
    public void sendEmail(String senderMailId, String receiverMailId, String senderName) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(senderMailId);
        simpleMailMessage.setTo(receiverMailId);
        String Subject = senderName + " has invited you to view file 'Liveasy'";
        simpleMailMessage.setSubject(Subject);
        String body = "Welcome to Liveasy, kindly login through the following url :- https://shipperwebapp.web.app/#/";
        simpleMailMessage.setText(body);
     

        this.mailSender.send(simpleMailMessage);
    }

	
}