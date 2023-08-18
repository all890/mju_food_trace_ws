package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.*;
import org.itsci.mju_food_trace_ws.repository.ManufacturerCertificateRepository;
import org.itsci.mju_food_trace_ws.repository.ManufacturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@Service
public class ManufacturerCertificateServiceImpl implements ManufacturerCertificateService {

    private final String MANUFACTURER_CERT_FOLDER_PATH = "C:/img/mncert/";

    @Autowired
    private ManufacturerCertificateRepository manufacturerCertificateRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;
    @Override
    public ManufacturerCertificate updateMnCertRegistStatus(String manuftId) {
        ManufacturerCertificate manufacturerCertificate = manufacturerCertificateRepository.getManufacturerCertificateByManufacturer_ManuftId(manuftId);
        manufacturerCertificate.setMnCertStatus("อนุมัติ");
        return manufacturerCertificateRepository.save(manufacturerCertificate);
    }

    @Override
    public ManufacturerCertificate saveRequestManufacturerCertificate(Map<String, String> map) throws ParseException {
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

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date mnCertRegDate = format.parse(mnCertRegDateStr);
        Date mnCertExpireDate = format.parse(mnCertExpireDateStr);
        String username = map.get("username");
        Manufacturer manufacturer = manufacturerRepository.getManufacturerByUser_Username(username);
        String mnCertStatus = "รอการอนุมัติ";

        manufacturerCertificate = new ManufacturerCertificate(mnCertId, mnCertImg, mnCertUploadDate, mnCertNo, mnCertRegDate, mnCertExpireDate, mnCertStatus, manufacturer);

        //Save manufacturer certificate data to database by using farmer manager and get result message
        return manufacturerCertificateRepository.save(manufacturerCertificate);
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
        return MANUFACTURER_CERT_FOLDER_PATH + newFileName;
    }

    @Override
    public Path downloadManufacturerCertificate(String filePath) {
        return new File(MANUFACTURER_CERT_FOLDER_PATH + filePath).toPath();
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
