package com.springboot.EmailSender.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springboot.EmailSender.Entities.EmailMessage;

@Repository
public interface EmailSenderDao extends JpaRepository<EmailMessage, Long> {
	
}