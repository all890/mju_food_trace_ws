package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.QRCode;

import java.nio.file.Path;

public interface QRCodeService {

    QRCode generateQRCode (String manufacturingId);
    Path downloadQRCode(String qrcodeId);

}
