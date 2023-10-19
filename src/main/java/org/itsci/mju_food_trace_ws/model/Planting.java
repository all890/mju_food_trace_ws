package org.itsci.mju_food_trace_ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "plantings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Planting {

    @Id
    private String plantingId;

    private String plantName;

    private Date plantDate;

    private String plantingImg;

    private String bioextract;

    private Date approxHarvDate;

    private String plantingMethod;

    private double netQuantity;

    private String netQuantityUnit;

    private double squareMeters;

    private double squareYards;

    private double rai;

    private String ptPrevBlockHash;

    private String ptCurrBlockHash;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fmCertId")
    private FarmerCertificate farmerCertificate;

}
