package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.QRCode;

import java.io.IOException;
import java.nio.file.Path;

public interface QRCodeService {

    QRCode generateQRCode (String manufacturingId) throws IOException;
    Path downloadQRCode(String qrcodeId);

}
