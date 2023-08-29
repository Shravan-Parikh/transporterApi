package com.springboot.EmailSender.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;

import com.google.cloud.Timestamp;

import lombok.Data;

@Entity
@Table(name = "sendEmail")
@Data
public class EmailMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	 
	 @NotBlank(message = "Email Cannot Be Empty")
	    private String receiverMailId;
	 @NotBlank(message = "Sender Name Cannot Be Empty")
	    private String senderName;

	 @CreationTimestamp
	    public Timestamp timestamp;
	 
	public enum emailSentStatus {
			SENT, NOT_SENT
		}

	  
}