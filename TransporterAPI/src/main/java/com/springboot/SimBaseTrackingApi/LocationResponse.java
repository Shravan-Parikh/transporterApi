package com.springboot.SimBaseTrackingApi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationResponse {
    private String latitude;
    private String longitude;
    private String serverTime;
    private String error="false";
}
