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

    private int squareMeters;

    private int squareYards;

    private int rai;

    private String ptPrevBlockHash;

    private String ptCurrBlockHash;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "farmerId")
    private Farmer farmer;

    public Planting(String plantingId, String plantName, Date plantDate, String plantingImg, String bioextract, Date approxHarvDate, String plantingMethod, double netQuantity, String netQuantityUnit, int squareMeters, int squareYards, int rai, Farmer farmer) {
        this.plantingId = plantingId;
        this.plantName = plantName;
        this.plantDate = plantDate;
        this.plantingImg = plantingImg;
        this.bioextract = bioextract;
        this.approxHarvDate = approxHarvDate;
        this.plantingMethod = plantingMethod;
        this.netQuantity = netQuantity;
        this.netQuantityUnit = netQuantityUnit;
        this.squareMeters = squareMeters;
        this.squareYards = squareYards;
        this.rai = rai;
        this.farmer = farmer;
    }
}
