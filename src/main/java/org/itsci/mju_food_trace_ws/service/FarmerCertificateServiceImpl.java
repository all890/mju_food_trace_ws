package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.FarmerCertificate;
import org.itsci.mju_food_trace_ws.model.User;
import org.itsci.mju_food_trace_ws.repository.FarmerCertificateRepository;
import org.itsci.mju_food_trace_ws.repository.FarmerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FarmerCertificateServiceImpl implements FarmerCertificateService {

    private final String FARMER_CERT_FOLDER_PATH = "C:/img/fmcert/";

    @Autowired
    private FarmerCertificateRepository farmerCertificateRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Override
    public FarmerCertificate saveFarmerCertificate(FarmerCertificate farmerCertificate) {
        return farmerCertificateRepository.save(farmerCertificate);
    }

    @Override
    public FarmerCertificate getFarmerCertificateById(String fmCertId) {
        return farmerCertificateRepository.getReferenceById(fmCertId);
    }

    @Override
    public List<FarmerCertificate> getFarmerCertificatesByFmCertStatus(String fmCertStatus) {
        return farmerCertificateRepository.getFarmerCertificatesByFmCertStatusEquals(fmCertStatus);
    }

    @Override
    public FarmerCertificate saveRequestFarmerCertificate(Map<String, String> map) throws ParseException, JsonProcessingException, NoSuchAlgorithmException {

        User user = null;

        FarmerCertificate farmerCertificate = null;


        //Farmer session
        String fmCertId = generateFarmerCertificateId(farmerCertificateRepository.count() + 1);
        System.out.println("FARMERCERT ID IS : " + fmCertId);
        String fmCertImg = map.get("fmCertImg");
        Date fmCertUploadDate = new Date();
        String fmCertNo = map.get("fmCertNo");


        String fmCertRegDateStr = map.get("fmCertRegDate");
        String fmCertExpireDateStr = map.get("fmCertExpireDate");


        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date fmCertRegDate = format.parse(fmCertRegDateStr);
        Date fmCertExpireDate = format.parse(fmCertExpireDateStr);
        String username = map.get("username");
        Farmer farmer = farmerRepository.getFarmerByUser_Username(username);
        String fmCertStatus = "รอการอนุมัติ";


        // farmer = new Farmer(farmerId, farmerName, farmerLastname, farmerEmail, farmerMobileNo, farmerRegDate, farmerRegStatus, farmName, farmLatitude, farmLongitude, user);


        farmerCertificate = new FarmerCertificate(fmCertId, fmCertImg, fmCertUploadDate, fmCertNo, fmCertRegDate, fmCertExpireDate, fmCertStatus, farmer.getFmCurrBlockHash(), null, farmer);

        /*
        //TODO: Generate current block hash by not using user data
        String jsonStr = new ObjectMapper().writeValueAsString(farmerCertificate);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
        String encodedFmCertCurrBlockHash = Base64.getEncoder().encodeToString(hash);

        farmerCertificate.setFmCertCurrBlockHash(encodedFmCertCurrBlockHash);
         */

        //farmerCertificateService.saveFarmerCertificate(farmerCertificate);

        //Save farmer certificate data to database by using farmer manager and get result message
        return farmerCertificateRepository.save(farmerCertificate);
    }

    @Override
    public List<FarmerCertificate> getFmCertsByFarmerUsername(String username) {
        return farmerCertificateRepository.getFarmerCertificatesByFarmer_User_Username(username);
    }

    @Override
    public boolean hasFmCertWaitToAccept(String username) {
        List<FarmerCertificate> farmerCertificates = farmerCertificateRepository.getFarmerCertificatesByFmCertStatusEquals("รอการอนุมัติ");
        return farmerCertificates.size() > 0;
    }

    @Override
    public String getNewFmCertCurrBlockHash(String fmCertId) throws JsonProcessingException, NoSuchAlgorithmException {
        FarmerCertificate farmerCertificate = farmerCertificateRepository.getReferenceById(fmCertId);

        farmerCertificate.setFarmer(null);
        farmerCertificate.setFmCertCurrBlockHash(null);

        String jsonStr = new ObjectMapper().writeValueAsString(farmerCertificate);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public String uploadFarmerCertificate(MultipartFile file) throws IOException {
        System.out.println("FILE NAME IS : " + file.getOriginalFilename());
        String newFileName = System.currentTimeMillis() + ".png";
        file.transferTo(new File(FARMER_CERT_FOLDER_PATH + newFileName));
        return newFileName;
    }

    @Override
    public Path downloadFarmerCertificate(String filePath) {
        return new File(FARMER_CERT_FOLDER_PATH + filePath).toPath();
    }

    @Override
    public String generateFarmerCertificateId (long rawId) {
        String result = Long.toString(rawId);
        while (result.length() < 8) {
            result = "0" + result;
        }
        result = "FMC" + result;
        return result;
    }

    @Override
    public FarmerCertificate updateFmCertRegistStatus(String farmerId, String fmCurrBlockHash) throws JsonProcessingException, NoSuchAlgorithmException {
        FarmerCertificate farmerCertificate = farmerCertificateRepository.getFarmerCertificateByFarmer_FarmerId(farmerId);
        farmerCertificate.setFmCertStatus("อนุมัติ");
        farmerCertificate.setFmCertPrevBlockHash(fmCurrBlockHash);

        Farmer tempFarmer = farmerCertificate.getFarmer();
        farmerCertificate.setFarmer(null);
        //User tempUser = farmerCertificate.getFarmer().getUser();
        //farmerCertificate.getFarmer().setUser(null);

        String jsonStr2 = new ObjectMapper().writeValueAsString(farmerCertificate);
        MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
        byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
        String encodedFmCertCurrBlockHash = Base64.getEncoder().encodeToString(hash2);

        farmerCertificate.setFmCertCurrBlockHash(encodedFmCertCurrBlockHash);
        farmerCertificate.setFarmer(tempFarmer);

        return farmerCertificateRepository.save(farmerCertificate);
    }
    @Override
    public FarmerCertificate updateFmCertRegistStatusDecline(String farmerId) {
        FarmerCertificate farmerCertificate = farmerCertificateRepository.getFarmerCertificateByFarmer_FarmerId(farmerId);
        farmerCertificate.setFmCertStatus("ไม่อนุมัติ");
        return farmerCertificateRepository.save(farmerCertificate);
    }

    @Override
    public FarmerCertificate updateFmRenewingRequetCertStatus(String fmCertId) throws JsonProcessingException, NoSuchAlgorithmException {
        FarmerCertificate farmerCertificate = farmerCertificateRepository.getReferenceById(fmCertId);
        farmerCertificate.setFmCertStatus("อนุมัติ");

        farmerCertificate.setFmCertPrevBlockHash(farmerCertificate.getFarmer().getFmCurrBlockHash());

        Farmer tempFarmer = farmerCertificate.getFarmer();
        farmerCertificate.setFarmer(null);
        //User tempUser = farmerCertificate.getFarmer().getUser();
        //farmerCertificate.getFarmer().setUser(null);

        String jsonStr2 = new ObjectMapper().writeValueAsString(farmerCertificate);
        MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
        byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
        String encodedFmCertCurrBlockHash = Base64.getEncoder().encodeToString(hash2);

        farmerCertificate.setFmCertCurrBlockHash(encodedFmCertCurrBlockHash);
        farmerCertificate.setFarmer(tempFarmer);

        return farmerCertificateRepository.save(farmerCertificate);
    }

    @Override
    public FarmerCertificate declineFmRenewingRequetCertStatus(String fmCertId) {
        FarmerCertificate farmerCertificate = farmerCertificateRepository.getReferenceById(fmCertId);
        farmerCertificate.setFmCertStatus("ไม่อนุมัติ");
        return farmerCertificateRepository.save(farmerCertificate);
    }

    @Override
    public FarmerCertificate getLatestFarmerCertificateByFarmerUsername(String username) {
        return farmerCertificateRepository.getLatestFarmerCertificateByFarmerUsername(username, "อนุมัติ");
    }

}
