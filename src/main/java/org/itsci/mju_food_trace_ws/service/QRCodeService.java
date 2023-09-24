package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.itsci.mju_food_trace_ws.model.QRCode;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public interface QRCodeService {

    QRCode generateQRCode (String manufacturingId) throws IOException;
    Path downloadQRCode(String qrcodeId);
    QRCode getProductDetailsByQRCodeId (String qrcodeId);
    boolean isWholeChainValid (QRCode qrCode) throws JsonProcessingException, NoSuchAlgorithmException;

}
