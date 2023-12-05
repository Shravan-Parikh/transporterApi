package com.springboot.EwayBillAPI.Entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "EwayBillDetails")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class EwayBillEntity {
    @Id
    private long ewbNo;
    private String ewayBillDate;
    private String genMode;
    private String userGstin;
    private String supplyType;
    private String subSupplyType;
    private String docType;
    private String docNo;
    private String docDate;
    private String fromGstin;
    private String fromTrdName;
    private String fromAddr1;
    private String fromAddr2;
    private String fromPlace;
    private long fromPincode;
    private long fromStateCode;
    private String toGstin;
    private String toTrdName;
    private String toAddr1;
    private String toAddr2;
    private String toPlace;
    private long toPincode;
    private long toStateCode;
    private double totalValue;
    private double totInvValue;
    private double cgstValue;
    private double sgstValue;
    private double igstValue;
    private double cessValue;
    private String transporterId;
    private String transporterName;
    private String status;
    private long actualDist;
    private long noValidDays;
    private String validUpto;
    private long extendedTimes;
    private String rejectStatus;
    private String vehicleType;
    private long actFromStateCode;
    private long actToStateCode;
    private long transactionType;
    private double otherValue;
    private double cessNonAdvolValue;

    @CreationTimestamp
	public Timestamp timestamp;
}
