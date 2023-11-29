package com.springboot.ShipperAPI.Response;

import java.sql.Timestamp;
import java.util.ArrayList;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.CreationTimestamp;

import com.springboot.ShipperAPI.Model.PostShipper.Roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipperUpdateResponse {
	private String status;
	private String message;

	private String shipperId;
	private String shipperName;
	private String companyName;
	private String phoneNo;
	private String emailId;
	private String gst;
	private String companyStatus;
	private String kyc;
	private String shipperLocation;
	private boolean companyApproved;
	private boolean accountVerificationInProgress;
	public Timestamp timestamp;

	private ArrayList<ArrayList<String>> transporterList;
	
	private String companyId; //optional
	public enum Roles {
		ADMIN, EDITOR, VIEWER
	}
	@Enumerated(EnumType.STRING)
    private Roles roles;
}
