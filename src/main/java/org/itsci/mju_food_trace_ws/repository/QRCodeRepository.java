package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QRCodeRepository extends JpaRepository<QRCode, String> {

    @Query(value = "SELECT qr.qrcodeId FROM qr_codes qr ORDER BY qr.qrcodeId DESC LIMIT 1", nativeQuery = true)
    String getMaxQRCodeId ();

}
