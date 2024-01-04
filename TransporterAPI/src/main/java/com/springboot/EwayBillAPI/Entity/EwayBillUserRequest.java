package com.springboot.EwayBillAPI.Entity;

import lombok.Data;

@Data
public class EwayBillUserRequest {
    private String username;
    private String password;
    private String gstin;
    private String role;
    private long stateCode;

}
