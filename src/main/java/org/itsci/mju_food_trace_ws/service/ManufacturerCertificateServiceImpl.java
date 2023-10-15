package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itsci.mju_food_trace_ws.model.*;
import org.itsci.mju_food_trace_ws.repository.ManufacturerCertificateRepository;
import org.itsci.mju_food_trace_ws.repository.ManufacturerRepository;
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
public class ManufacturerCertificateServiceImpl implements ManufacturerCertificateService {

    //MANUFACTURER_CERT path
    private final String MANUFACTURER_CERT_FOLDER_PATH = "C:/img/mncert/";

    @Autowired
    private ManufacturerCertificateRepository manufacturerCertificateRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Override
    public ManufacturerCertificate updateMnCertRegistStatus(String manuftId, String mnCurrBlockHash) throws JsonProcessingException, NoSuchAlgorithmException {
        ManufacturerCertificate manufacturerCertificate = manufacturerCertificateRepository.getManufacturerCertificateByManufacturer_ManuftId(manuftId);
        manufacturerCertificate.setMnCertStatus("อนุมัติ");
        manufacturerCertificate.setMnCertPrevBlockHash(mnCurrBlockHash);

        Manufacturer tempManufacturer = manufacturerCertificate.getManufacturer();
        manufacturerCertificate.setManufacturer(null);
        //User tempUser = manufacturerCertificate.getManufacturer().getUser();
        //manufacturerCertificate.getManufacturer().setUser(null);

        //TODO: Generate current block hash by not using user data
        String jsonStr2 = new ObjectMapper().writeValueAsString(manufacturerCertificate);
        MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
        byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
        String encodedMnCertCurrBlockHash = Base64.getEncoder().encodeToString(hash2);

        manufacturerCertificate.setMnCertCurrBlockHash(encodedMnCertCurrBlockHash);
        manufacturerCertificate.setManufacturer(tempManufacturer);

        return manufacturerCertificateRepository.save(manufacturerCertificate);
    }

    @Override
    public ManufacturerCertificate declineMnCertRegistStatus(String manuftId) {
        ManufacturerCertificate manufacturerCertificate = manufacturerCertificateRepository.getManufacturerCertificateByManufacturer_ManuftId(manuftId);
        manufacturerCertificate.setMnCertStatus("ไม่อนุมัติ");
        return manufacturerCertificateRepository.save(manufacturerCertificate);
    }

    @Override
    public ManufacturerCertificate updateMnRenewingRequetCertStatus(String mnCertId) throws JsonProcessingException, NoSuchAlgorithmException {
        ManufacturerCertificate manufacturerCertificate = manufacturerCertificateRepository.getReferenceById(mnCertId);
        manufacturerCertificate.setMnCertStatus("อนุมัติ");

        manufacturerCertificate.setMnCertPrevBlockHash(manufacturerCertificate.getManufacturer().getMnCurrBlockHash());

        Manufacturer tempManufacturer = manufacturerCertificate.getManufacturer();
        manufacturerCertificate.setManufacturer(null);
        //User tempUser = manufacturerCertificate.getManufacturer().getUser();
        //manufacturerCertificate.getManufacturer().setUser(null);

        String jsonStr2 = new ObjectMapper().writeValueAsString(manufacturerCertificate);
        MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
        byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
        String encodedMnCertCurrBlockHash = Base64.getEncoder().encodeToString(hash2);

        manufacturerCertificate.setMnCertCurrBlockHash(encodedMnCertCurrBlockHash);
        manufacturerCertificate.setManufacturer(tempManufacturer);
        return manufacturerCertificateRepository.save(manufacturerCertificate);
    }

    @Override
    public ManufacturerCertificate declineMnRenewingRequetCertStatus(String mnCertId) {
        ManufacturerCertificate manufacturerCertificate = manufacturerCertificateRepository.getReferenceById(mnCertId);
        manufacturerCertificate.setMnCertStatus("ไม่อนุมัติ");
        return manufacturerCertificateRepository.save(manufacturerCertificate);
    }

    @Override
    public ManufacturerCertificate getLatestManufacturerCertificateByManufacturerUsername(String username) {
        return manufacturerCertificateRepository.getLatestManufacturerCertificateByManufacturerUsername(username,"อนุมัติ");
    }

    @Override
    public ManufacturerCertificate saveRequestManufacturerCertificate(Map<String, String> map) throws ParseException, JsonProcessingException, NoSuchAlgorithmException {
        User user = null;

        ManufacturerCertificate manufacturerCertificate = null;

        //Manufacturer session
        String mnCertId = generateManufacturerCertificateId(manufacturerCertificateRepository.count() + 1);
        System.out.println("MANUFACTURERCERT ID IS : " + mnCertId);
        String mnCertImg = map.get("mnCertImg");
        Date mnCertUploadDate = new Date();
        String mnCertNo = map.get("mnCertNo");

        String mnCertRegDateStr = map.get("mnCertRegDate");
        String mnCertExpireDateStr = map.get("mnCertExpireDate");

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date mnCertRegDate = format.parse(mnCertRegDateStr);
        Date mnCertExpireDate = format.parse(mnCertExpireDateStr);
        String username = map.get("username");
        Manufacturer manufacturer = manufacturerRepository.getManufacturerByUser_Username(username);
        String mnCertStatus = "รอการอนุมัติ";

        manufacturerCertificate = new ManufacturerCertificate(mnCertId, mnCertImg, mnCertUploadDate, mnCertNo, mnCertRegDate, mnCertExpireDate, mnCertStatus, manufacturer.getMnCurrBlockHash(), null, manufacturer);

        /*
        //TODO: Generate current block hash by not using user data
        String jsonStr = new ObjectMapper().writeValueAsString(manufacturerCertificate);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
        String encodedMnCertCurrBlockHash = Base64.getEncoder().encodeToString(hash);

        manufacturerCertificate.setMnCertCurrBlockHash(encodedMnCertCurrBlockHash);
        */

        //Save manufacturer certificate data to database by using farmer manager and get result message
        return manufacturerCertificateRepository.save(manufacturerCertificate);
    }

    @Override
    public List<ManufacturerCertificate> getMnCertsByManufacturerUsername(String username) {
        return manufacturerCertificateRepository.getManufacturerCertificateByManufacturer_User_Username(username);
    }

    @Override
    public boolean hasMnCertWaitToAccept(String username) {
        List<ManufacturerCertificate> manufacturerCertificates = manufacturerCertificateRepository.getManufacturerCertificatesByMnCertStatusEquals("รอการอนุมัติ");
        return manufacturerCertificates.size() > 0;
    }

    @Override
    public String getNewMnCertCurrBlockHash(String mnCertId) throws JsonProcessingException, NoSuchAlgorithmException {
        ManufacturerCertificate manufacturerCertificate = manufacturerCertificateRepository.getReferenceById(mnCertId);
        manufacturerCertificate.setManufacturer(null);
        manufacturerCertificate.setMnCertCurrBlockHash(null);

        String jsonStr = new ObjectMapper().writeValueAsString(manufacturerCertificate);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public ManufacturerCertificate saveManufacturerCertificate(ManufacturerCertificate manufacturerCertificate) {
        return manufacturerCertificateRepository.save(manufacturerCertificate);
    }

    @Override
    public String uploadManufacturerCertificate(MultipartFile file) throws IOException {
        System.out.println("FILE NAME IS : " + file.getOriginalFilename());
        String newFileName = System.currentTimeMillis() + ".png";
        file.transferTo(new File(MANUFACTURER_CERT_FOLDER_PATH + newFileName));
        return newFileName;
    }

    @Override
    public ManufacturerCertificate getManufacturerCertificateById(String mnCertId) {
        return manufacturerCertificateRepository.getReferenceById(mnCertId);
    }

    @Override
    public Path downloadManufacturerCertificate(String filePath) {
        return new File(MANUFACTURER_CERT_FOLDER_PATH + filePath).toPath();
    }

    @Override
    public List<ManufacturerCertificate> getManuftCertificatesByMnCertStatus(String mnCertStatus) {
        return manufacturerCertificateRepository.getManufacturerCertificatesByMnCertStatusEquals(mnCertStatus);
    }

    public String generateManufacturerCertificateId (long rawId) {
        String result = Long.toString(rawId);
        while (result.length() < 8) {
            result = "0" + result;
        }
        result = "MNC" + result;
        return result;
    }

}
