package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.FarmerCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface FarmerCertificateRepository extends JpaRepository<FarmerCertificate, String> {

    FarmerCertificate getFarmerCertificateByFarmer_FarmerId (String farmerId);

    List<FarmerCertificate> getFarmerCertificatesByFmCertStatusEquals (String fmCertStatus);

    @Query(value = "SELECT * FROM farmer_certificates fmc WHERE " +
            "fmc.farmer_id = (SELECT fm.farmer_id FROM farmers fm WHERE fm.username = ?1) AND fmc.fm_cert_status = ?2 " +
            "ORDER BY fmc.fm_cert_id DESC LIMIT 1", nativeQuery = true)
    FarmerCertificate getLatestFarmerCertificateByFarmerUsername (String username, String status);

    List<FarmerCertificate> getFarmerCertificatesByFarmer_User_Username (String username);

    List<FarmerCertificate> getFarmerCertificatesByFarmer_User_UsernameAndFmCertStatusEquals (String username, String fmCertStatus);

}
