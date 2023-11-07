package org.itsci.mju_food_trace_ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {

    @Id
    private String productId;

    private String productName;

    private int netVolume;

    private int netEnergy;

    private int saturatedFat;

    private int cholesterol;

    private int protein;

    private int sodium;

    private int fiber;

    private int sugar;

    private int vitA;

    private int vitB1;

    private int vitB2;

    private int iron;

    private int calcium;

    private String pdPrevBlockHash;

    private String pdCurrBlockHash;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "manuftId")
    private Manufacturer manufacturer;

}
