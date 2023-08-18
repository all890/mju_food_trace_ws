package org.itsci.mju_food_trace_ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "manufacturers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Manufacturer {

    @Id
    private String manuftId;

    private String manuftName;

    private String manuftEmail;

    private Date manuftRegDate;

    private String manuftRegStatus;

    private String factoryLatitude;

    private String factoryLongitude;

    private String factoryTelNo;

    private String factorySupName;

    private String factorySupLastname;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username")
    private User user;

}
