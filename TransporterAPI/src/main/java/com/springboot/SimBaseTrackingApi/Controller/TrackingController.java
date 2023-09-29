package com.springboot.SimBaseTrackingApi.Controller;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.SimBaseTrackingApi.Entity.TrackingEntity;
import com.springboot.SimBaseTrackingApi.Service.TrackingService;
import com.springboot.SimBaseTrackingApi.Status.ConsentStatus;

@RestController
public class TrackingController {

    @Autowired
    public TrackingService service;

    @PostMapping("/consent")
    public ResponseEntity<ConsentStatus> ConsentResponse(@Valid @RequestBody TrackingEntity entity) 
    throws IOException, URISyntaxException{
        return new ResponseEntity<>(service.getConsentResponse(entity), HttpStatus.OK);
    }

    @GetMapping("/consentStatus/{mobileNumber}")
    public ResponseEntity<ConsentStatus> getConsentStatusResponse(@PathVariable String mobileNumber) 
    throws IOException{
        return new ResponseEntity<>(service.getConsentStatus(mobileNumber), HttpStatus.OK);
    }

}
