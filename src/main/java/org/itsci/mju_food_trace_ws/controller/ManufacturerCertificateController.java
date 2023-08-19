package org.itsci.mju_food_trace_ws.controller;

import org.itsci.mju_food_trace_ws.model.FarmerCertificate;
import org.itsci.mju_food_trace_ws.model.ManufacturerCertificate;
import org.itsci.mju_food_trace_ws.service.ManufacturerCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manuftcertificate")
public class ManufacturerCertificateController {

    @Autowired
    private ManufacturerCertificateService manufacturerCertificateService;

    @RequestMapping("/upload")
    public ResponseEntity uploadFarmerCertificate (@RequestParam("image") MultipartFile file) throws IllegalStateException, IOException {
        try {
            String filePath = manufacturerCertificateService.uploadManufacturerCertificate(file);
            return new ResponseEntity<>(filePath, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getlistmncertrenewreq")
    public ResponseEntity getListMnCertRenewReq () {
        try {
            List<ManufacturerCertificate> manufacturerCertificates = manufacturerCertificateService.getManuftCertificatesByMnCertStatus("รอการอนุมัติ");
            System.out.println(manufacturerCertificates.size());
            return new ResponseEntity<>(manufacturerCertificates, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list mn cert req renew!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/{filePath}")
    public byte[] downloadManufacturerCertificate (@PathVariable("filePath") String filePath) throws IOException {
        byte[] image = Files.readAllBytes(manufacturerCertificateService.downloadManufacturerCertificate(filePath));
        return image;
    }
    @RequestMapping("/addmncert")
    public ResponseEntity addMnCert (@RequestBody Map<String, String> map) {
        try {
            ManufacturerCertificate manufacturercert = manufacturerCertificateService.saveRequestManufacturerCertificate(map);
            return new ResponseEntity<>(manufacturercert, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save manufacturer certificate data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
