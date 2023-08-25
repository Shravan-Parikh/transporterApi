package com.springboot.EmailSender.Service;

public interface EmailSenderService {
	public emailSentStatus sendEmail(String receiverMailId, String senderName);
	
}
