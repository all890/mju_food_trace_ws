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

    @GetMapping("/getlatestmncertbyusername/{username}")
    public ResponseEntity getLatestMnCertByManufacturerUsername (@PathVariable("username") String username) {
        try {
            ManufacturerCertificate manufacturerCertificate = manufacturerCertificateService.getLatestManufacturerCertificateByManufacturerUsername(username);
            return new ResponseEntity<>(manufacturerCertificate, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get latest mn cert by username.", HttpStatus.INTERNAL_SERVER_ERROR);
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
    @GetMapping("/getmncertdetails/{mnCertId}")
    public ResponseEntity getManufactuerCertificateDetails (@PathVariable("mnCertId") String mnCertId) {
        try {
            ManufacturerCertificate manufacturerCertificate = manufacturerCertificateService.getManufacturerCertificateById(mnCertId);
            return new ResponseEntity<>(manufacturerCertificate , HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get manufacturer certificate details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/updatemnrenewingrequestcert/{mnCertId}")
    public ResponseEntity updateMnRenewingReqCert (@PathVariable("mnCertId") String mnCertId) {
        try {
            System.out.println("UPDATE MANUFACTURER RENEWING REQUET CERT");
            ManufacturerCertificate manufacturerCertificate = manufacturerCertificateService.updateMnRenewingRequetCertStatus(mnCertId);
            return new ResponseEntity<>(manufacturerCertificate, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update manufacturer renewing requet certificate status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/declinemnrenewingrequestcert/{mnCertId}")
    public ResponseEntity declineFmRenewingReqCert (@PathVariable("mnCertId") String mnCertId) {
        try {
            System.out.println(mnCertId);
            System.out.println("DECLINE MANUFACTURER RENEWING REQUET CERT");
            ManufacturerCertificate manufacturerCertificate = manufacturerCertificateService.declineMnRenewingRequetCertStatus(mnCertId);
            return new ResponseEntity<>(manufacturerCertificate, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("DECLINE to update manufacturer renewing requet certificate status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getmncertsbyusername/{username}")
    public ResponseEntity getMnCertsByManufacturerUsername (@PathVariable("username") String username) {
        try {
            List<ManufacturerCertificate> manufacturerCertificates = manufacturerCertificateService.getMnCertsByManufacturerUsername(username);
            return new ResponseEntity<>(manufacturerCertificates, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get mn certs by username", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/haswaittoacceptcert/{username}")
    public ResponseEntity hasMnCertWaitToAccept (@PathVariable("username") String username) {
        try {
            if (manufacturerCertificateService.hasMnCertWaitToAccept(username)) {
                return new ResponseEntity<>("This user has mn cert wait to accept", HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>("This user hasn't any mn cert wait to accept", HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to having cert wait to accept", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ischainvalbefmncert/{username}")
    public ResponseEntity isChainValidBeforeMnCert (@PathVariable("username") String username) {
        try {
            if (manufacturerCertificateService.isChainBeforeMnCertValid(username)) {
                return new ResponseEntity<>("Chain before mn cert is valid", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Chain before mn cert isn't valid", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to validate chain before mn cert", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
