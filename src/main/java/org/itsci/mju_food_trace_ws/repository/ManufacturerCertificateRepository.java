package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.ManufacturerCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManufacturerCertificateRepository extends JpaRepository<ManufacturerCertificate, String> {

    ManufacturerCertificate getManufacturerCertificateByManufacturer_ManuftId (String manuftId);

    List<ManufacturerCertificate> getManufacturerCertificatesByMnCertStatusEquals (String mnCertStatus);

}
