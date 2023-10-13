package org.itsci.mju_food_trace_ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "farmers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Farmer {

    @Id
    private String farmerId;

    private String farmerName;

    private String farmerLastname;

    private String farmerEmail;

    private String farmerMobileNo;

    private Date farmerRegDate;

    private String farmerRegStatus;

    private String farmName;

    private String farmLatitude;

    private String farmLongitude;

    private String fmPrevBlockHash;

    private String fmCurrBlockHash;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username")
    private User user;

}
