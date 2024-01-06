package com.springboot.EmailSender.Entities;

import java.sql.Timestamp;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;


import lombok.Data;

@Entity
@Table(name = "sendEmail")
@Data
public class EmailMessage {

    @Id
    private String inviteId;
	 
	 @NotBlank(message = "Email Cannot Be Empty")
	    private String receiverMailId;
	 @NotBlank(message = "Sender Name Cannot Be Empty")
	    private String companyName;
	 private String companyId;     // optional
	@Enumerated(EnumType.STRING)
	 private Roles roles;		   // optional

	 @CreationTimestamp
	    public Timestamp timestamp;

	 public enum Roles {            //optional
		 ADMIN, EDITOR, VIEWER
	 }
	 public enum emailSentStatus {
			SENT, NOT_SENT
	 }

	  
}
