package org.itsci.mju_food_trace_ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "manufacturings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Manufacturing {

    @Id
    private String manufacturingId;

    private Date manufactureDate;

    private Date expireDate;

    private int productQty;

    private String productUnit;

    private double usedRawMatQty;

    private String usedRawMatQtyUnit;

    private String manuftPrevBlockHash;

    private String manuftCurrBlockHash;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rawMatShpId")
    private RawMaterialShipping rawMaterialShipping;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "productId")
    private Product product;

}
