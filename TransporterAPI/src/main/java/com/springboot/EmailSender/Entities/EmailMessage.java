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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	 
	 @NotBlank(message = "Email Cannot Be Empty")
	    private String receiverMailId;
	 @NotBlank(message = "Sender Name Cannot Be Empty")
	    private String companyName;
	 private String companyId;     // optional
	@Enumerated(EnumType.STRING)
	 private roles role;		   // optional

	 @CreationTimestamp
	    public Timestamp timestamp;

	 public enum roles{            //optional
		 ADMIN, EDITOR, VIEWER
	 }
	 public enum emailSentStatus {
			SENT, NOT_SENT
	 }

	  
}
