package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.itsci.mju_food_trace_ws.model.*;
import org.itsci.mju_food_trace_ws.repository.ManufacturingRepository;
import org.itsci.mju_food_trace_ws.repository.QRCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    private final String QRCODE_FOLDER_PATH = "C:/img/qrcode/";

    @Autowired
    private QRCodeRepository qrCodeRepository;

    @Autowired
    private ManufacturingRepository manufacturingRepository;

    @Override
    public QRCode generateQRCode(String manufacturingId) throws IOException {
        Manufacturing manufacturing = manufacturingRepository.getReferenceById(manufacturingId);
        Date generateDate = new Date();

        /*
        String maxQRCodeId = qrCodeRepository.getMaxQRCodeId();
        long maxQRCodeLong = 0;

        if (maxQRCodeId != null) {
            maxQRCodeLong = Long.parseLong(maxQRCodeId.substring(4));
        }
        */

        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        // Create a StringBuilder to build the random string
        StringBuilder randomStringBuilder = new StringBuilder();

        // Create an instance of the Random class
        Random random = new Random();

        // Generate random characters and append them to the StringBuilder
        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomStringBuilder.append(randomChar);
        }

        String qrcodeId = randomStringBuilder.toString();

        String qrcodeImg = generateQRCodeImage(qrcodeId);

        QRCode qrCode = new QRCode(qrcodeId, qrcodeImg, generateDate, manufacturing);
        return qrCodeRepository.save(qrCode);
    }

    @Override
    public Path downloadQRCode(String qrcodeId) {
        return new File(QRCODE_FOLDER_PATH + qrcodeId + ".jpg").toPath();
    }

    @Override
    public QRCode getProductDetailsByQRCodeId(String qrcodeId) {
        QRCode qrCode = null;
        if (qrCodeRepository.existsById(qrcodeId)) {
            qrCode = qrCodeRepository.getReferenceById(qrcodeId);
            return qrCode;
        }
        return qrCode;
    }

    @Override
    public Map<String, String> isWholeChainValid(QRCode qrCode) throws JsonProcessingException, NoSuchAlgorithmException {

        Map<String, String> incorrectData = new HashMap<>();

        //Separate each blocks follow the order
        Planting planting = qrCode.getManufacturing().getRawMaterialShipping().getPlanting();
        RawMaterialShipping rawMaterialShipping = qrCode.getManufacturing().getRawMaterialShipping();
        Manufacturing manufacturing = qrCode.getManufacturing();

        //First floor, check planting current block hash
        User tempFmUser = planting.getFarmerCertificate().getFarmer().getUser();
        String oldPtCurrBlockHash = planting.getPtCurrBlockHash();
        planting.getFarmerCertificate().getFarmer().setUser(null);
        planting.setPtCurrBlockHash(null);

        String jsonStr = new ObjectMapper().writeValueAsString(planting);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
        String newPtCurrBlockHash = Base64.getEncoder().encodeToString(hash);

        planting.getFarmerCertificate().getFarmer().setUser(tempFmUser);
        planting.setPtCurrBlockHash(oldPtCurrBlockHash);

        //First floor checking (oldPtCurrBlockHash and newPtCurrBlockHash)
        System.out.println("OLD PLANTING CURR BLOCK HASH : " + oldPtCurrBlockHash);
        System.out.println("NEW PLANTING CURR BLOCK HASH : " + newPtCurrBlockHash);
        if (oldPtCurrBlockHash.equals(newPtCurrBlockHash)) {
            //Second floor checking (oldPtCurrBlockHash and rmsPrevBlockHash)
            if (oldPtCurrBlockHash.equals(rawMaterialShipping.getRmsPrevBlockHash())) {
                User tempFmUser2 = rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().getUser();
                User tempMnUser = rawMaterialShipping.getManufacturer().getUser();
                String oldRmsCurrBlockHash = rawMaterialShipping.getRmsCurrBlockHash();
                rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setUser(null);
                rawMaterialShipping.getManufacturer().setUser(null);
                rawMaterialShipping.setRmsCurrBlockHash(null);

                String jsonStr2 = new ObjectMapper().writeValueAsString(rawMaterialShipping);
                MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
                byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
                String newRmsCurrBlockHash = Base64.getEncoder().encodeToString(hash2);

                rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setUser(tempFmUser2);
                rawMaterialShipping.getManufacturer().setUser(tempMnUser);
                rawMaterialShipping.setRmsCurrBlockHash(oldRmsCurrBlockHash);

                //Third floor checking (oldRmsCurrBlockHash and newRmsCurrBlockHash)
                System.out.println("OLD RMS CURR BLOCK HASH : " + oldRmsCurrBlockHash);
                System.out.println("NEW RMS CURR BLOCK HASH : " + newRmsCurrBlockHash);
                if (oldRmsCurrBlockHash.equals(newRmsCurrBlockHash)) {
                    //Fourth floor checking (oldRmsCurrBlockHash and manuftPrevBlockHash)
                    if (oldRmsCurrBlockHash.equals(manufacturing.getManuftPrevBlockHash())) {
                        User tempFmUser3 = manufacturing.getRawMaterialShipping().getPlanting().getFarmerCertificate().getFarmer().getUser();
                        User tempMnUser2 = manufacturing.getProduct().getManufacturer().getUser();
                        String oldManuftCurrBlockHash = manufacturing.getManuftCurrBlockHash();
                        manufacturing.getRawMaterialShipping().getPlanting().getFarmerCertificate().getFarmer().setUser(null);
                        manufacturing.getProduct().getManufacturer().setUser(null);
                        manufacturing.getRawMaterialShipping().getManufacturer().setUser(null);
                        manufacturing.getManufacturerCertificate().getManufacturer().setUser(null);
                        manufacturing.setManuftCurrBlockHash(null);

                        String jsonStr3 = new ObjectMapper().writeValueAsString(manufacturing);
                        MessageDigest digest3 = MessageDigest.getInstance("SHA-256");
                        byte[] hash3 = digest3.digest(jsonStr3.getBytes(StandardCharsets.UTF_8));
                        String newManuftCurrBlockHash = Base64.getEncoder().encodeToString(hash3);

                        manufacturing.getRawMaterialShipping().getPlanting().getFarmerCertificate().getFarmer().setUser(tempFmUser3);
                        manufacturing.getProduct().getManufacturer().setUser(tempMnUser2);
                        manufacturing.getRawMaterialShipping().getManufacturer().setUser(tempMnUser2);
                        manufacturing.getManufacturerCertificate().getManufacturer().setUser(tempMnUser2);
                        manufacturing.setManuftCurrBlockHash(oldManuftCurrBlockHash);

                        System.out.println("OLD MANUFT CURR BLOCK HASH : " + oldManuftCurrBlockHash);
                        System.out.println("NEW MANUFT CURR BLOCK HASH : " + newManuftCurrBlockHash);
                        if (oldManuftCurrBlockHash.equals(newManuftCurrBlockHash)) {
                            return incorrectData;
                        } else {
                            incorrectData.put("5","MANUFACTURING CURRENT BLOCK HASH");
                            System.out.println("MANUFACTURING CURRENT BLOCK HASH WAS TAMPERED");
                            return incorrectData;
                        }
                    } else {
                        incorrectData.put("4","MANUFACTURING PREVIOUS BLOCK HASH");
                        System.out.println("MANUFACTURING PREVIOUS BLOCK HASH WAS TAMPERED");
                        return incorrectData;
                    }
                } else {
                    incorrectData.put("3","RMS CURRENT BLOCK HASH");
                    System.out.println("RMS CURRENT BLOCK HASH WAS TAMPERED");
                    return incorrectData;
                }
            } else {
                incorrectData.put("2","RMS PREVIOUS BLOCK HASH");
                System.out.println("RMS PREVIOUS BLOCK HASH WAS TAMPERED");
                return incorrectData;
            }
        } else {
            incorrectData.put("1","PLANTING CURRENT BLOCK HASH");
            System.out.println("PLANTING CURRENT BLOCK HASH WAS TAMPERED");
            return incorrectData;
        }

    }

    private String generateQRCodeImage (String qrcodeId) throws IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hintsMap = new HashMap<>();
        hintsMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(qrcodeId, BarcodeFormat.QR_CODE, 350, 350, hintsMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate QR Code image!");
        }
        BufferedImage qrImage = toBufferedImage(bitMatrix, qrcodeId);

        Path outputDir = Paths.get(QRCODE_FOLDER_PATH);

        String fileName = qrcodeId + ".jpg";
        Path outputFile = outputDir.resolve(fileName);
        try {
            ImageIO.write(qrImage, "jpg", outputFile.toFile());
            //ImageIO.write(concatenatedImage, "jpg", outputFile.toFile());
        } catch (Exception e) {
            throw new RuntimeException("Failed to write QR Code image!");
        }

        return fileName;
    }

    private BufferedImage toBufferedImage(BitMatrix matrix, String qrcodeId) throws IOException {
        int width = matrix.getWidth();
        int height = matrix.getHeight()+100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height-100; y++) {
                if (matrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }

        BufferedImage image2 = ImageIO.read(new File("C:/img/logo.png"));


        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        graphics.drawImage(image2, 30, 320, 100, 90,null);
        graphics.setFont(new Font("Itim", Font.BOLD, 16));
        graphics.setColor(Color.BLACK);
        graphics.drawString("รหัส QR : "+qrcodeId, 125, 380);

        return image;
    }

    public String generateQRCodeId (long rawId) {
        String result = Long.toString(rawId);
        while (result.length() < 6) {
            result = "0" + result;
        }
        result = "FTQR" + result;
        return result;
    }
}
