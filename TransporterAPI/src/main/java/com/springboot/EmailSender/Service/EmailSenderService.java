package com.springboot.EmailSender.Service;

public interface EmailSenderService {
	public emailSentStatus sendEmail(String senderMailId, String receiverMailId, String senderName);
	
}
