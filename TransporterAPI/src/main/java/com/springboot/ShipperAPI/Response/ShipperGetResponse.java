package com.springboot.ShipperAPI.Response;

import com.springboot.ShipperAPI.Entity.Shipper;
import com.springboot.ShipperAPI.Model.PostShipper.Roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipperGetResponse {
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
    private ArrayList<ArrayList<String>> transporterList;
    
    private String companyId; //optional
    
    @Enumerated(EnumType.STRING)
    private Roles roles;
    
	public enum Roles {
		ADMIN, EDITOR, VIEWER
	}
	
	
}
