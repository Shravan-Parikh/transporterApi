package com.springboot.ShipperAPI.Response;

import com.springboot.ShipperAPI.Entity.Shipper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipperGetResponse {

    private Shipper shipper;

    private ArrayList<ArrayList<String>> emailList;

}
