package com.springboot.SimBaseTrackingApi.Entity;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackingEntity {
    @NotBlank(message = "Mobile Number cannot be blank!")
    private String mobileNumber;
    private int consentDurationInDays;
    private String driverName;
    private String operatorName;
}
