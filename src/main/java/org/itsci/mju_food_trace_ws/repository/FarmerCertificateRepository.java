package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.FarmerCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmerCertificateRepository extends JpaRepository<FarmerCertificate, String> {

    FarmerCertificate getFarmerCertificateByFarmer_FarmerId (String farmerId);

    List<FarmerCertificate> getFarmerCertificatesByFmCertStatusEquals (String fmCertStatus);

}
