package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.itsci.mju_food_trace_ws.model.FarmerCertificate;
import org.itsci.mju_food_trace_ws.model.ManufacturerCertificate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ManufacturerCertificateService {

    ManufacturerCertificate saveManufacturerCertificate(ManufacturerCertificate manufacturerCertificate);
    String uploadManufacturerCertificate (MultipartFile file) throws IOException;
    ManufacturerCertificate getManufacturerCertificateById (String mnCertId);
    Path downloadManufacturerCertificate(String filePath);
    List<ManufacturerCertificate> getManuftCertificatesByMnCertStatus(String mnCertStatus);
    String generateManufacturerCertificateId (long rawId);
    ManufacturerCertificate updateMnCertRegistStatus(String manuftId) throws JsonProcessingException, NoSuchAlgorithmException;
    ManufacturerCertificate declineMnCertRegistStatus(String manuftId);
    ManufacturerCertificate updateMnRenewingRequetCertStatus(String mnCertId) throws JsonProcessingException, NoSuchAlgorithmException;
    ManufacturerCertificate declineMnRenewingRequetCertStatus(String mnCertId);
    ManufacturerCertificate getLatestManufacturerCertificateByManufacturerUsername(String username);
    ManufacturerCertificate saveRequestManufacturerCertificate(Map<String, String> map) throws ParseException, JsonProcessingException, NoSuchAlgorithmException;

    List<ManufacturerCertificate> getMnCertsByManufacturerUsername (String username);
    boolean hasMnCertWaitToAccept (String username);

}
