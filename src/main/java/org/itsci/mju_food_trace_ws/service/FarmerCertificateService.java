package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.FarmerCertificate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface FarmerCertificateService {

    FarmerCertificate saveFarmerCertificate (FarmerCertificate farmerCertificate);
    FarmerCertificate getFarmerCertificateById (String fmCertId);
    List<FarmerCertificate> getFarmerCertificatesByFmCertStatus (String fmCertStatus);
    String uploadFarmerCertificate (MultipartFile file) throws IOException;
    Path downloadFarmerCertificate(String filePath);
    String generateFarmerCertificateId (long rawId);
    FarmerCertificate updateFmCertRegistStatus(String farmerId);
    FarmerCertificate updateFmCertRegistStatusDecline(String farmerId);
    FarmerCertificate updateFmRenewingRequetCertStatus(String fmCertId);
    FarmerCertificate declineFmRenewingRequetCertStatus(String fmCertId);
    FarmerCertificate getLatestFarmerCertificateByFarmerUsername(String username);
    FarmerCertificate saveRequestFarmerCertificate(Map<String, String> map) throws ParseException;

}
