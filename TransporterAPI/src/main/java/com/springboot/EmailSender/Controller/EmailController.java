package com.springboot.EmailSender.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        this.emailSenderService.sendEmail(emailMessage);
        return ResponseEntity.ok("Invite Email Successfully Send");
    }

    @GetMapping("/inviteEmails")
    public ResponseEntity<?> getInviteDetails(@RequestParam String inviteId){
        EmailMessage emailMessage = emailSenderService.getInviteDetails(inviteId);
        if (emailMessage != null){
            return new ResponseEntity<>(emailMessage, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("InviteId does not Exists",HttpStatus.NOT_FOUND);
        }
    }
} 