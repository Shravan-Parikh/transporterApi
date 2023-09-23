package com.springboot.SimBaseTrackingApi.Entity;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Entity {
    @NotBlank
    private String mobileNumber;
    private int consentDurationInDays;
    private String customerName;
    private String operatorName;
}
