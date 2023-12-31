package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itsci.mju_food_trace_ws.model.*;
import org.itsci.mju_food_trace_ws.repository.ManufacturerCertificateRepository;
import org.itsci.mju_food_trace_ws.repository.ManufacturerRepository;
import org.itsci.mju_food_trace_ws.utils.HashUtil;
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

        User mnCertUser = manufacturerCertificate.getManufacturer().getUser();
        manufacturerCertificate.getManufacturer().setUser(null);
        manufacturerCertificate.setMnCertPrevBlockHash(mnCurrBlockHash);
        String mnCertCurrBlockHash = HashUtil.hashSHA256(manufacturerCertificate);
        manufacturerCertificate.getManufacturer().setUser(mnCertUser);
        manufacturerCertificate.setMnCertCurrBlockHash(mnCertCurrBlockHash);

        return manufacturerCertificateRepository.save(manufacturerCertificate);
    }

    @Override
    public ManufacturerCertificate declineMnCertRegistStatus(String manuftId, String mnCurrBlockHash) throws NoSuchAlgorithmException, JsonProcessingException {
        ManufacturerCertificate manufacturerCertificate = manufacturerCertificateRepository.getManufacturerCertificateByManufacturer_ManuftId(manuftId);
        manufacturerCertificate.setMnCertStatus("ไม่อนุมัติ");

        User mnCertUser = manufacturerCertificate.getManufacturer().getUser();
        manufacturerCertificate.getManufacturer().setUser(null);
        manufacturerCertificate.setMnCertPrevBlockHash(mnCurrBlockHash);
        String mnCertCurrBlockHash = HashUtil.hashSHA256(manufacturerCertificate);
        manufacturerCertificate.getManufacturer().setUser(mnCertUser);
        manufacturerCertificate.setMnCertCurrBlockHash(mnCertCurrBlockHash);

        return manufacturerCertificateRepository.save(manufacturerCertificate);
    }

    @Override
    public ManufacturerCertificate updateMnRenewingRequetCertStatus(String mnCertId) throws JsonProcessingException, NoSuchAlgorithmException {
        ManufacturerCertificate manufacturerCertificate = manufacturerCertificateRepository.getReferenceById(mnCertId);
        manufacturerCertificate.setMnCertStatus("อนุมัติ");
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

        manufacturerCertificate = new ManufacturerCertificate(mnCertId, mnCertImg, mnCertUploadDate, mnCertNo, mnCertRegDate, mnCertExpireDate, mnCertStatus, null, null, manufacturer);

        //Save manufacturer certificate data to database by using farmer manager and get result message
        return manufacturerCertificateRepository.save(manufacturerCertificate);
    }

    @Override
    public List<ManufacturerCertificate> getMnCertsByManufacturerUsername(String username) {
        return manufacturerCertificateRepository.getManufacturerCertificateByManufacturer_User_Username(username);
    }

    @Override
    public boolean hasMnCertWaitToAccept(String username) {
        List<ManufacturerCertificate> manufacturerCertificates = manufacturerCertificateRepository.getManufacturerCertificatesByMnCertStatusEqualsAndManufacturer_User_Username("รอการอนุมัติ", username);
        System.out.println("WAIT TO ACCEPT SIZE : " + manufacturerCertificates.size());
        return manufacturerCertificates.size() > 0;
    }

    @Override
    public boolean isChainBeforeMnCertValid(String username) throws NoSuchAlgorithmException, JsonProcessingException {
        Manufacturer manufacturer = manufacturerRepository.getManufacturerByUser_Username(username);

        User tempMnUser = manufacturer.getUser();
        String oldMnCurrBlockHash = manufacturer.getMnCurrBlockHash();

        manufacturer.setUser(null);
        manufacturer.setMnCurrBlockHash(null);

        String newMnCurrBlockHash = HashUtil.hashSHA256(manufacturer);

        manufacturer.setUser(tempMnUser);
        manufacturer.setMnCurrBlockHash(oldMnCurrBlockHash);

        return newMnCurrBlockHash.equals(oldMnCurrBlockHash);
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
