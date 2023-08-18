package org.itsci.mju_food_trace_ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "administrators")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Administrator {

    @Id
    private String adminId;

    private String adminName;

    private String adminLastname;

    private String adminMobileNo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username")
    private User user;

}
