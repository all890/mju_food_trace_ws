package org.itsci.mju_food_trace_ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "qr_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QRCode {

    @Id
    private String qrcodeId;

    private String qrcodeImg;

    private Date generateDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "manufacturingId")
    private Manufacturing manufacturing;

}
