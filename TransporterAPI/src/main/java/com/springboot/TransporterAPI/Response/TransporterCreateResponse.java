package com.springboot.TransporterAPI.Response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransporterCreateResponse {
	private String status;
	private String message;

	private String transporterId;
	private String phoneNo;
	private String transporterName;
	private String companyName;
	private String transporterLocation;
	private String kyc;
	private String emailId;
	private Boolean companyApproved;
	private Boolean transporterApproved;
	private Boolean accountVerificationInProgress;
	public Timestamp timestamp;
}
