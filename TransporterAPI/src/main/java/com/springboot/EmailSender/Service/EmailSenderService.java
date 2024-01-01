package com.springboot.EmailSender.Service;

import com.springboot.EmailSender.Entities.EmailMessage;
import com.springboot.EmailSender.Entities.EmailMessage.emailSentStatus;

public interface EmailSenderService {
	public emailSentStatus sendEmail(EmailMessage emailMessage);
	public EmailMessage getInviteDetails(String inviteId);
	
}