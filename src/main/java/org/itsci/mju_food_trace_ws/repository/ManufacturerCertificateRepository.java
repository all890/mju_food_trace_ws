package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.FarmerCertificate;
import org.itsci.mju_food_trace_ws.model.ManufacturerCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ManufacturerCertificateRepository extends JpaRepository<ManufacturerCertificate, String> {

    ManufacturerCertificate getManufacturerCertificateByManufacturer_ManuftId (String manuftId);

    List<ManufacturerCertificate> getManufacturerCertificatesByMnCertStatusEquals (String mnCertStatus);
    List<ManufacturerCertificate> getManufacturerCertificatesByMnCertStatusEqualsAndManufacturer_User_Username (String mnCertStatus, String username);

    @Query(value = "SELECT * FROM manufacturer_certificates mfc WHERE " +
            "mfc.manuftId = (SELECT mn.manuftId FROM manufacturers mn WHERE mn.username = ?1) AND mfc.mnCertStatus = ?2 " +
            "ORDER BY mfc.mnCertId DESC LIMIT 1", nativeQuery = true)
    ManufacturerCertificate getLatestManufacturerCertificateByManufacturerUsername (String username,String status);

    List<ManufacturerCertificate> getManufacturerCertificateByManufacturer_User_Username (String username);

}
