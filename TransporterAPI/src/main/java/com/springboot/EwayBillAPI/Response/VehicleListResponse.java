package com.springboot.EwayBillAPI.Response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleListResponse {
    private String updMode;
    private String vehicleNo;
    private String fromPlace;
    private long fromState;
    private long tripshtNo;
    private String userGSTINTransin;
    private String enteredDate;
    private String transMode;
    private String transDocNo;
    private String transDocDate;
    private String groupNo;
}
