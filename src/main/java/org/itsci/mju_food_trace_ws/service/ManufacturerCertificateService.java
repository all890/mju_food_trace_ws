package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.FarmerCertificate;
import org.itsci.mju_food_trace_ws.model.ManufacturerCertificate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
    ManufacturerCertificate updateMnCertRegistStatus(String manuftId);
    ManufacturerCertificate declineMnCertRegistStatus(String manuftId);
    ManufacturerCertificate getLatestManufacturerCertificateByManufacturerUsername(String username);
    ManufacturerCertificate saveRequestManufacturerCertificate(Map<String, String> map) throws ParseException;
}
