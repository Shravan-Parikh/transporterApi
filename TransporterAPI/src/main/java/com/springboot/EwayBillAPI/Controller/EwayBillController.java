package com.springboot.EwayBillAPI.Controller;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.validation.Valid;

import com.springboot.EwayBillAPI.Entity.EwayBillEntity;
import com.springboot.EwayBillAPI.Entity.EwayBillUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springboot.EwayBillAPI.Entity.EwayBillUsers;
import com.springboot.EwayBillAPI.Service.EwayBillService;

@RestController
public class EwayBillController {
    
    @Autowired
    public EwayBillService service;

    @PostMapping("/ewayBillUser")
    public ResponseEntity<Object> SaveCredentialsResponse(@Valid @RequestBody EwayBillUsers entity) 
    throws IOException, URISyntaxException{
        return new ResponseEntity<>(service.SaveCredentials(entity), HttpStatus.OK);
    }

    @GetMapping("/ewayBillUser/{userId}")
    public ResponseEntity<Object> userDetails(@PathVariable String userId){
        return new ResponseEntity<>(service.getUserDetails(userId), HttpStatus.OK);
    }

    @GetMapping("/ewayBill")
    public ResponseEntity<Object> ewayBillDetails(
        @RequestParam(required = false) Long ewbNo,
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String fromGstin,
        @RequestParam(required = false) String toGstin,
        @RequestParam(required = false) String transporterGstin,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate
    ){
        return new ResponseEntity<Object>(service.getEwayBill(ewbNo, userId, fromGstin,
        toGstin, transporterGstin, fromDate, toDate), HttpStatus.OK);
    }

    @PutMapping("/updateEwayBill")
    public ResponseEntity<Object> updateEwayBillUser(@RequestParam String userId, @RequestBody EwayBillUserRequest requestEntity){
        Object object= service.updateEwayBillUser(userId, requestEntity);
        if (object.getClass() == "".getClass()){
            return new ResponseEntity<>(object, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(object, HttpStatus.OK);
    }
}
