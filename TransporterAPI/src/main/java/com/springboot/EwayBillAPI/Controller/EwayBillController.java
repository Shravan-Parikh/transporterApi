package com.springboot.EwayBillAPI.Controller;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/ewayBill")
    public ResponseEntity<Object> ewayBillDetails(
        @RequestParam(required = false) Long ewbNo,
        @RequestParam(required = false) String fromGstin,
        @RequestParam(required = false) String toGstin,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate
    ){
        return new ResponseEntity<Object>(service.getEwayBill(ewbNo, fromGstin, 
        toGstin, fromDate, toDate), HttpStatus.OK);
    }
}
