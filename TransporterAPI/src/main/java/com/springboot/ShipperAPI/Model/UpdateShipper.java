package com.springboot.ShipperAPI.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShipper {
	private String phoneNo;
	private String emailId;
	private String shipperName;
	private String companyName;
	private String kyc;
	private String gst;
	private String companyStatus;
	private String shipperLocation;
	private Boolean companyApproved;
	private Boolean accountVerificationInProgress;

	private ArrayList<ArrayList<String>> transporterList;
	
	private String companyId; //optional
	public enum roles {
		ADMIN, EDITOR, VIEWER
	}
	@Enumerated(EnumType.STRING)
    private roles roles;
}
