package com.springboot.EwayBillAPI.Response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemListResponse {
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
}
