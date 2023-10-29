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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FarmerServiceImpl implements FarmerService {

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private FarmerCertificateRepository farmerCertificateRepository;

    @Autowired
    private FarmerCertificateService farmerCertificateService;

    @Override
    public List<Farmer> getAllFarmers() {
        return farmerRepository.findAll();
    }

    @Override
    public List<Farmer> getFarmersByRegistStat() {
        return farmerRepository.getFarmersByFarmerRegStatus("รอการอนุมัติ");
    }

    @Override
    public Farmer getFarmerById(String farmerId) {
        return farmerRepository.getReferenceById(farmerId);
    }

    @Override
    public Farmer saveFarmer(Map<String, String> map) throws ParseException, NoSuchAlgorithmException, JsonProcessingException {

        User user = null;
        Farmer farmer = null;
        FarmerCertificate farmerCertificate = null;

        //User session
        String username = map.get("username");
        String password = map.get("password");
        System.out.println("PASSWORD : " + password);
        String userType = "FARMER";
        user = new User(username, password, userType);

        //Farmer session
        String farmerId = generateFarmerId(farmerRepository.count() + 1);
        System.out.println("FARMER ID IS : " + farmerId);
        String farmerName = map.get("farmerName");
        String farmerLastname = map.get("farmerLastname");
        String farmerEmail = map.get("farmerEmail");
        String farmerMobileNo = map.get("farmerMobileNo");
        Date farmerRegDate = new Date();
        String farmerRegStatus = "รอการอนุมัติ";
        String farmName = map.get("farmName");
        String farmLatitude = map.get("farmLatitude");
        String farmLongitude = map.get("farmLongitude");

        //Farmer reg status, user must be null before
        farmer = new Farmer(farmerId, farmerName, farmerLastname, farmerEmail, farmerMobileNo, farmerRegDate, farmerRegStatus, farmName, farmLatitude, farmLongitude, user);

        String fmCertId = farmerCertificateService.generateFarmerCertificateId(farmerCertificateRepository.count() + 1);
        String fmCertImg = map.get("fmCertImg");
        Date fmCertUploadDate = new Date();
        String fmCertNo = map.get("fmCertNo");
        String fmCertRegDateStr = map.get("fmCertRegDate");
        String fmCertExpireDateStr = map.get("fmCertExpireDate");

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date fmCertRegDate = format.parse(fmCertRegDateStr);
        Date fmCertExpireDate = format.parse(fmCertExpireDateStr);

        String fmCertStatus = "รอการอนุมัติครั้งแรก";

        farmerCertificate = new FarmerCertificate(fmCertId, fmCertImg, fmCertUploadDate, fmCertNo, fmCertRegDate, fmCertExpireDate, fmCertStatus, farmer);

        //farmerCertificate.setFmCertCurrBlockHash(encodedFmCertCurrBlockHash);

        farmerCertificateService.saveFarmerCertificate(farmerCertificate);

        //Save farmer certificate data to database by using farmer manager and get result message
        return farmerRepository.save(farmer);
    }

    @Override
    public Farmer updateFarmer(Farmer farmer) {
        return farmerRepository.save(farmer);
    }

    @Override
    public Farmer updateFmRegistStatus(String farmerId) throws JsonProcessingException, NoSuchAlgorithmException {
        Farmer farmer = farmerRepository.getReferenceById(farmerId);
        farmer.setFarmerRegStatus("อนุมัติ");
        farmerCertificateService.updateFmCertRegistStatus(farmerId);
        return farmerRepository.save(farmer);
    }

    @Override
    public Farmer declineFmRegistStatus(String farmerId) {
        Farmer farmer = farmerRepository.getReferenceById(farmerId);
        farmer.setFarmerRegStatus("ไม่อนุมัติ");
        farmerCertificateService.updateFmCertRegistStatusDecline(farmerId);
        return farmerRepository.save(farmer);
    }

    @Override
    public FarmerCertificate getFarmerDetails(String farmerId) {
        FarmerCertificate farmerCertificate = farmerCertificateRepository.getFarmerCertificateByFarmer_FarmerId(farmerId);
        return farmerCertificate;
    }

    @Override
    public void deleteFarmer(String farmerId) {
        Farmer farmer = farmerRepository.getReferenceById(farmerId);
        farmerRepository.delete(farmer);
    }

    @Override
    public Farmer getFarmerByUsername(String username) {
        return farmerRepository.getFarmerByUser_Username(username);
    }

    @Override
    public Farmer getFarmerByFarmerMobileNo(String farmerMobileNo) {
        return farmerRepository.getFarmerByFarmerMobileNo(farmerMobileNo);
    }

    public String generateFarmerId (long rawId) {
        String result = Long.toString(rawId);
        while (result.length() < 8) {
            result = "0" + result;
        }
        result = "FM" + result;
        return result;
    }

    //Test Comment From James
    //Test Comment From Tle

}
