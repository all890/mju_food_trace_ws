package org.itsci.mju_food_trace_ws.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "manufacturer_certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ManufacturerCertificate {

    @Id
    private String mnCertId;

    private String mnCertImg;

    private Date mnCertUploadDate;

    private String mnCertNo;

    private Date mnCertRegDate;

    private Date mnCertExpireDate;

    private String mnCertStatus;

    private String mnCertPrevBlockHash;

    private String mnCertCurrBlockHash;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "manuftId")
    private Manufacturer manufacturer;

}
