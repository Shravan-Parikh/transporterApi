package com.springboot.SimBaseTrackingApi.Controller;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.SimBaseTrackingApi.LocationResponse;
import com.springboot.SimBaseTrackingApi.Entity.Entity;
import com.springboot.SimBaseTrackingApi.Service.TrackingService;
import com.springboot.SimBaseTrackingApi.Status.ConsentStatus;

@RestController
public class TrackingController {

    @Autowired
    public TrackingService service;

    @PostMapping("/consent")
    public ResponseEntity<Object> ConsentResponse(@Valid @RequestBody Entity entity) 
    throws IOException{
        return new ResponseEntity<>(service.getConsentResponse(entity), HttpStatus.OK);
    }

    @GetMapping("/activateTracking/{mobileNumber}")
    public ResponseEntity<String> startTrackingResponse(@PathVariable String mobileNumber) 
    throws IOException, URISyntaxException{
        return new ResponseEntity<>(service.getStartTrackingResponse(mobileNumber), HttpStatus.OK);
    }

    @GetMapping("/consentStatus/{mobileNumber}")
    public ResponseEntity<ConsentStatus> getConsentStatusResponse(@PathVariable String mobileNumber) 
    throws IOException{
        return new ResponseEntity<>(service.getConsentStatus(mobileNumber), HttpStatus.OK);
    }

    @GetMapping("/location/{mobileNumber}")
    public ResponseEntity<LocationResponse> TrackingResponse(@PathVariable String mobileNumber) 
    throws IOException, InterruptedException{
        return new ResponseEntity<>(service.getTrackingResponse(mobileNumber), HttpStatus.OK);
    }
}
