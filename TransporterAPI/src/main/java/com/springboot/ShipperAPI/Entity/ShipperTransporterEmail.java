package com.springboot.ShipperAPI.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="shipper_transporter_email")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipperTransporterEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "TransportId Cannot Be Empty")
    private String transporterId;
    @NotBlank(message = "Email Cannot Be Empty")
    private String email;

    @NotBlank(message = "Name Cannot Be Empty")
    private String name;

    @NotBlank(message = "Phone Number Cannot Be Empty")
    private String phoneNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id", foreignKey = @ForeignKey(name = "FK_transporter_shipper"))
    private Shipper shipper;
}
