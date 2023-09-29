package com.springboot.SimBaseTrackingApi.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsentStatus {
    private String status="Unknown";
    private String error="false";
}
