package com.springboot.EmailSender.Service;

import com.springboot.EmailSender.Entities.EmailMessage.emailSentStatus;

public interface EmailSenderService {
	public emailSentStatus sendEmail(String receiverMailId, String senderName);
	
}