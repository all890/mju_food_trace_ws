package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.itsci.mju_food_trace_ws.model.QRCode;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface QRCodeService {

    QRCode generateQRCode (String manufacturingId) throws IOException;
    Path downloadQRCode(String qrcodeId);
    QRCode getProductDetailsByQRCodeId (String qrcodeId);
    Map<String, String> isWholeChainValid (QRCode qrCode) throws JsonProcessingException, NoSuchAlgorithmException;

}
