package org.itsci.mju_food_trace_ws.controller;

import org.itsci.mju_food_trace_ws.model.QRCode;
import org.itsci.mju_food_trace_ws.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@RestController
@RequestMapping("/qrcode")
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;

    @GetMapping("/generate/{manufacturingId}")
    public ResponseEntity generateQRCode (@PathVariable("manufacturingId") String manufacturingId) {
        try {
            QRCode qrCode = qrCodeService.generateQRCode(manufacturingId);
            return new ResponseEntity<>(qrCode, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to generate QR Code!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ischainvalid/{qrcodeId}")
    public ResponseEntity isWholeChainValid (@PathVariable("qrcodeId") String qrcodeId) {
        try {
            QRCode qrCode = qrCodeService.getProductDetailsByQRCodeId(qrcodeId);
            Map<String, String> map = qrCodeService.isWholeChainValid(qrCode);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to validate chain!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getproddetails/{qrcodeId}")
    public ResponseEntity getProductDetailsByQRCodeId (@PathVariable("qrcodeId") String qrcodeId) {
        try {
            QRCode qrCode = qrCodeService.getProductDetailsByQRCodeId(qrcodeId);
            if (qrCode != null) {
                return new ResponseEntity<>(qrCode, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Not found this product by qr code", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to product details by qr code!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/getqrcodebyid/{qrcodeId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] downloadQRCode (@PathVariable("qrcodeId") String qrcodeId) throws IOException {
        byte[] image = Files.readAllBytes(qrCodeService.downloadQRCode(qrcodeId));
        return image;
    }

}
