package com.springboot.EmailSender.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.EmailSender.Entities.EmailMessage;
import com.springboot.EmailSender.Service.EmailSenderService;

@CrossOrigin
@RestController
public class EmailController {

    private final EmailSenderService emailSenderService;

    public EmailController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

  
    
    @PostMapping("/sendInviteEmail")
    public ResponseEntity<String> sendEmail(@RequestBody EmailMessage emailMessage) {
        this.emailSenderService.sendEmail(emailMessage.getReceiverMailId(), emailMessage.getSenderName());
        return ResponseEntity.ok("Invite Email Successfully Send");
    }
} 