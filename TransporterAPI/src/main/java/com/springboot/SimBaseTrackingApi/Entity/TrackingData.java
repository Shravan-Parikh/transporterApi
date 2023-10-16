package com.springboot.SimBaseTrackingApi.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TrackingData")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TrackingData {
    @Id
    @GeneratedValue
    private Long trackingId;
    @NotBlank
    @Column(unique = true)
    private String mobileNumber;
    private String driverName;
    private String operatorName;
    private String status;
}
