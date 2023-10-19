package org.itsci.mju_food_trace_ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "raw_material_shippings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RawMaterialShipping {

    @Id
    private String rawMatShpId;

    private Date rawMatShpDate;

    private double rawMatShpQty;

    private String rawMatShpQtyUnit;

    private Date receiveDate;

    private String status;

    private String rmsPrevBlockHash;

    private String rmsCurrBlockHash;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "plantingId")
    private Planting planting;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "manuftId")
    private Manufacturer manufacturer;

}
