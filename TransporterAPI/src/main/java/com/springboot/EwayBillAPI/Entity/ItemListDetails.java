package com.springboot.EwayBillAPI.Entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "EwayBillItemList")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ItemListDetails {

    @Id
    @GeneratedValue
    private long itemListId;

    private long itemNo;
    private long productId;
    private String productName;
    private String productDesc;
    private long hsnCode;
    private double quantity;
    private String qtyUnit;
    private double cgstRate;
    private double sgstRate;
    private double igstRate;
    private double cessRate;
    private double cessNonAdvol;
    private double taxableAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ewbNo", foreignKey = @ForeignKey(name = "FK_EwayBillEwbNo"))
    private EwayBillEntity ewayBillDetails;
}
