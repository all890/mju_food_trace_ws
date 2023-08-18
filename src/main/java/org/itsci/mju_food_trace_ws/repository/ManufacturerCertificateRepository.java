package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.ManufacturerCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturerCertificateRepository extends JpaRepository<ManufacturerCertificate, String> {

    ManufacturerCertificate getManufacturerCertificateByManufacturer_ManuftId (String manuftId);

}
