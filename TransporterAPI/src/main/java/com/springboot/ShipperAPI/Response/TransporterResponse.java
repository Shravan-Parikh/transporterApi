package com.springboot.ShipperAPI.Response;

import com.springboot.ShipperAPI.Entity.Shipper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransporterResponse {
	
    private String transporterId;
    private String name;
    private String phoneNo;
    private String email;
    private String companyName;
    private String gst;
    private String companyStatus;
    private String kyc;
    private String shipperLocation;
    private boolean companyApproved;
    private boolean accountVerificationInProgress;
}
