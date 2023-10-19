package org.itsci.mju_food_trace_ws.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "farmer_certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FarmerCertificate {

    @Id
    private String fmCertId;

    private String fmCertImg;

    private Date fmCertUploadDate;

    private String fmCertNo;

    private Date fmCertRegDate;

    private Date fmCertExpireDate;

    private String fmCertStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "farmerId")
    private Farmer farmer;

}
