package com.springboot.EmailSender.Service;

public interface EmailSenderService {
	public void sendEmail(String senderMailId, String receiverMailId, String senderName);

}
