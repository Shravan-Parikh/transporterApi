package com.springboot.EwayBillAPI.Entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "EwayBillVehicleList")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class VehicleListDetails {

    @Id
    @GeneratedValue
    private long vehicleListId;
    
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ewbNo", foreignKey = @ForeignKey(name = "FK_EwayBillEwbNo"))
    private EwayBillEntity ewayBillDetails;
}
