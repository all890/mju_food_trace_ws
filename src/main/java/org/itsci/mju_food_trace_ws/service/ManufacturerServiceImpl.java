package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.Manufacturer;
import org.itsci.mju_food_trace_ws.model.ManufacturerCertificate;
import org.itsci.mju_food_trace_ws.model.User;
import org.itsci.mju_food_trace_ws.repository.ManufacturerCertificateRepository;
import org.itsci.mju_food_trace_ws.repository.ManufacturerRepository;
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
public class ManufacturerServiceImpl implements ManufacturerService {

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private ManufacturerCertificateRepository manufacturerCertificateRepository;

    @Autowired
    private ManufacturerCertificateService manufacturerCertificateService;

    @Override
    public List<Manufacturer> getAllManufacturers() {return manufacturerRepository.findAll();
    }

    @Override
    public List<Manufacturer> getManufacturersByRegistStat() {
        return manufacturerRepository.getManufacturersByManuftRegStatus("รอการอนุมัติ");
    }

    @Override
    public Manufacturer getManufacturerById(String manuftId) {
        return manufacturerRepository.getReferenceById(manuftId);
    }

    @Override
    public Manufacturer updateMnRegistStatus(String manuftId) throws NoSuchAlgorithmException, JsonProcessingException {
        Manufacturer manufacturer = manufacturerRepository.getReferenceById(manuftId);
        manufacturer.setManuftRegStatus("อนุมัติ");

        User tempUser = manufacturer.getUser();
        manufacturer.setUser(null);

        //TODO: Generate current block hash by not using user data
        String jsonStr2 = new ObjectMapper().writeValueAsString(manufacturer);
        MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
        byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
        String encodedMnCurrBlockHash = Base64.getEncoder().encodeToString(hash2);

        manufacturer.setMnCurrBlockHash(encodedMnCurrBlockHash);
        manufacturer.setUser(tempUser);

        manufacturerCertificateService.updateMnCertRegistStatus(manuftId, manufacturer.getMnCurrBlockHash());
        return manufacturerRepository.save(manufacturer);
    }

    @Override
    public Manufacturer declineMnRegistStatus(String manuftId) {
        Manufacturer manufacturer = manufacturerRepository.getReferenceById(manuftId);
        manufacturer.setManuftRegStatus("ไม่อนุมัติ");
        manufacturerCertificateService.declineMnCertRegistStatus(manuftId);
        return manufacturerRepository.save(manufacturer);
    }

    @Override
    public ManufacturerCertificate getManufacturerDetails(String manuftId) {
        ManufacturerCertificate manufacturerCertificate = manufacturerCertificateRepository.getManufacturerCertificateByManufacturer_ManuftId(manuftId);
        return manufacturerCertificate;
    }

    @Override
    public Manufacturer saveManufacturer(Map<String, String> map) throws ParseException, JsonProcessingException, NoSuchAlgorithmException {

        System.out.println("SAVE MANUFACTURER!");

        Manufacturer manufacturer = null;
        User user = null;
        ManufacturerCertificate manufacturerCertificate = null;

        //User session
        String username = map.get("username");
        String password = map.get("password");
        String userType = "MANUFT";
        user = new User(username, password, userType);

        //Manufacturer session
        String manuftId = generateManufacturerId(manufacturerRepository.count() + 1);
        String manuftName = map.get("manuftName");
        String manuftEmail = map.get("manuftEmail");
        Date manuftRegDate = new Date();
        String manuftRegStatus = "รอการอนุมัติ";
        String factoryLatitude = map.get("factoryLatitude");
        String factoryLongitude = map.get("factoryLongitude");
        String factoryTelNo = map.get("factoryTelNo");
        String factorySupName = map.get("factorySupName");
        String factorySupLastname = map.get("factorySupLastname");

        manufacturer = new Manufacturer(manuftId, manuftName, manuftEmail, manuftRegDate, manuftRegStatus, factoryLatitude, factoryLongitude, factoryTelNo, factorySupName, factorySupLastname, "0", null, user);

        //Manufacturer certificate session
        String mnCertId = manufacturerCertificateService.generateManufacturerCertificateId(manufacturerCertificateRepository.count() + 1);
        String mnCertImg = map.get("mnCertImg");
        Date mnCertUploadDate = new Date();
        String mnCertNo = map.get("mnCertNo");
        String mnCertRegDateStr = map.get("mnCertRegDate");
        String mnCertExpireDateStr = map.get("mnCertExpireDate");

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date mnCertRegDate = format.parse(mnCertRegDateStr);
        Date mnCertExpireDate = format.parse(mnCertExpireDateStr);

        String mnCertStatus = "รอการอนุมัติ";

        manufacturerCertificate = new ManufacturerCertificate(mnCertId, mnCertImg, mnCertUploadDate, mnCertNo, mnCertRegDate, mnCertExpireDate, mnCertStatus, null, null, manufacturer);

        manufacturerCertificateService.saveManufacturerCertificate(manufacturerCertificate);

        return manufacturerRepository.save(manufacturer);
    }

    @Override
    public Manufacturer updateManufacturer(Manufacturer manufacturer) {
        return null;
    }

    @Override
    public void deleteManufacturer(String manuftId) {
        Manufacturer manufacturer = manufacturerRepository.getReferenceById(manuftId);
        manufacturerRepository.delete(manufacturer);
    }

    @Override
    public Manufacturer getManufacturerByUsername(String username) {
        return manufacturerRepository.getManufacturerByUser_Username(username);
    }

    @Override
    public boolean isManufacturerAvailable(String manuftName) {
        Manufacturer manufacturer = manufacturerRepository.getManufacturerByManuftNameEquals(manuftName);
        return manufacturer != null;
    }

    @Override
    public String getNewMnCurrBlockHash(String manuftId) throws JsonProcessingException, NoSuchAlgorithmException {
        Manufacturer manufacturer = manufacturerRepository.getReferenceById(manuftId);
        manufacturer.setUser(null);
        manufacturer.setMnCurrBlockHash(null);

        String jsonStr = new ObjectMapper().writeValueAsString(manufacturer);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(hash);
    }

    public String generateManufacturerId (long rawId) {
        String result = Long.toString(rawId);
        while (result.length() < 8) {
            result = "0" + result;
        }
        result = "MN" + result;
        return result;
    }
}
