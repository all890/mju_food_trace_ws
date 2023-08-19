package org.itsci.mju_food_trace_ws.controller;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.FarmerCertificate;
import org.itsci.mju_food_trace_ws.service.FarmerCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/farmercertificate")
public class FarmerCertificateController {

    @Autowired
    @Lazy
    private FarmerCertificateService farmerCertificateService;

    @RequestMapping("/upload")
    public ResponseEntity uploadFarmerCertificate (@RequestParam("image") MultipartFile file) throws IllegalStateException, IOException {
        try {
            String filePath = farmerCertificateService.uploadFarmerCertificate(file);
            return new ResponseEntity<>(filePath, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/{filePath}")
    public byte[] downloadFarmerCertificate (@PathVariable("filePath") String filePath) throws IOException {
        byte[] image = Files.readAllBytes(farmerCertificateService.downloadFarmerCertificate(filePath));
        return image;
    }

    @RequestMapping("/addfmcert")
    public ResponseEntity addFmCert (@RequestBody Map<String, String> map) {
        try {
            FarmerCertificate farmercert = farmerCertificateService.saveRequestFarmerCertificate(map);
            return new ResponseEntity<>(farmercert, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save farmer certificate data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getlistfmcertrenewreq")
    public ResponseEntity getListFmCertRenewReq () {
        try {
            List<FarmerCertificate> farmerCertificates = farmerCertificateService.getFarmerCertificatesByFmCertStatus("รอการอนุมัติ");
            System.out.println(farmerCertificates.size());
            return new ResponseEntity<>(farmerCertificates, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list fm cert req renew!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getfmcertbyid/{fmCertId}")
    public ResponseEntity getFmCertById (@PathVariable("fmCertId") String fmCertId) {
        try {
            FarmerCertificate farmerCertificate = farmerCertificateService.getFarmerCertificateById(fmCertId);
            return new ResponseEntity<>(farmerCertificate, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get farmer certificate by id!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
