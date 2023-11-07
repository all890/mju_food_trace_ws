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
import org.itsci.mju_food_trace_ws.utils.HashUtil;
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

        RawMaterialShipping rawMaterialShipping = qrCode.getManufacturing().getRawMaterialShipping();
        ManufacturerCertificate manufacturerCertificate = qrCode.getManufacturing().getManufacturerCertificate();
        Product product = qrCode.getManufacturing().getProduct();
        Manufacturing manufacturing = qrCode.getManufacturing();

        User tempFmUser = rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().getUser();
        String oldFmCurrBlockHash = rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().getFmCurrBlockHash();
        rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setFmCurrBlockHash(null);
        rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setUser(null);
        String newFmCurrBlockHash = HashUtil.hashSHA256(rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer());
        rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setFmCurrBlockHash(oldFmCurrBlockHash);
        rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setUser(tempFmUser);
        if (newFmCurrBlockHash.equals(oldFmCurrBlockHash)) {
            if (newFmCurrBlockHash.equals(rawMaterialShipping.getPlanting().getFarmerCertificate().getFmCertPrevBlockHash())) {
                User tempFmCertUser = rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().getUser();
                String oldFmCertCurrBlockHash = rawMaterialShipping.getPlanting().getFarmerCertificate().getFmCertCurrBlockHash();
                rawMaterialShipping.getPlanting().getFarmerCertificate().setFmCertCurrBlockHash(null);
                rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setUser(null);
                String newFmCertCurrBlockHash = HashUtil.hashSHA256(rawMaterialShipping.getPlanting().getFarmerCertificate());
                rawMaterialShipping.getPlanting().getFarmerCertificate().setFmCertCurrBlockHash(oldFmCertCurrBlockHash);
                rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setUser(tempFmCertUser);
                if (newFmCertCurrBlockHash.equals(oldFmCertCurrBlockHash)) {
                    if (newFmCertCurrBlockHash.equals(rawMaterialShipping.getPlanting().getPtPrevBlockHash())) {
                        User tempPtUser = rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().getUser();
                        String oldPtCurrBlockHash = rawMaterialShipping.getPlanting().getPtCurrBlockHash();
                        rawMaterialShipping.getPlanting().setPtCurrBlockHash(null);
                        rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setUser(null);
                        String newPtCurrBlockHash = HashUtil.hashSHA256(rawMaterialShipping.getPlanting());
                        rawMaterialShipping.getPlanting().setPtCurrBlockHash(oldPtCurrBlockHash);
                        rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setUser(tempPtUser);
                        if (newPtCurrBlockHash.equals(oldPtCurrBlockHash)) {
                            if (newPtCurrBlockHash.equals(rawMaterialShipping.getRmsPrevBlockHash())) {
                                User tempFmRmsUser = rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().getUser();
                                User tempMnRmsUser = rawMaterialShipping.getManufacturer().getUser();
                                String oldRmsCurrBlockHash = rawMaterialShipping.getRmsCurrBlockHash();
                                rawMaterialShipping.setRmsCurrBlockHash(null);
                                rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setUser(null);
                                rawMaterialShipping.getManufacturer().setUser(null);
                                String newRmsCurrBlockHash = HashUtil.hashSHA256(rawMaterialShipping);
                                rawMaterialShipping.setRmsCurrBlockHash(oldRmsCurrBlockHash);
                                rawMaterialShipping.getPlanting().getFarmerCertificate().getFarmer().setUser(tempFmRmsUser);
                                rawMaterialShipping.getManufacturer().setUser(tempMnRmsUser);
                                if (newRmsCurrBlockHash.equals(oldRmsCurrBlockHash)) {

                                } else {
                                    incorrectData.put("7","RMS CURRENT BLOCK HASH");
                                    System.out.println("E7");
                                    return incorrectData;
                                }
                            } else {
                                incorrectData.put("6","RMS PREVIOUS BLOCK HASH");
                                System.out.println("E6");
                                return incorrectData;
                            }
                        } else {
                            incorrectData.put("5","PLANTING CURRENT BLOCK HASH");
                            System.out.println("E5");
                            return incorrectData;
                        }
                    } else {
                        incorrectData.put("4","PLANTING PREVIOUS BLOCK HASH");
                        System.out.println("E4");
                        return incorrectData;
                    }
                } else {
                    incorrectData.put("3","FARMER CERTIFICATE CURRENT BLOCK HASH");
                    System.out.println("E3");
                    return incorrectData;
                }
            } else {
                incorrectData.put("2","FARMER CERTIFICATE PREVIOUS BLOCK HASH");
                System.out.println("E2");
                return incorrectData;
            }
        } else {
            incorrectData.put("1","FARMER CURRENT BLOCK HASH");
            System.out.println("E1");
            return incorrectData;
        }

        User tempMnUser = manufacturerCertificate.getManufacturer().getUser();
        String oldMnCurrBlockHash = manufacturerCertificate.getManufacturer().getMnCurrBlockHash();
        manufacturerCertificate.getManufacturer().setMnCurrBlockHash(null);
        manufacturerCertificate.getManufacturer().setUser(null);
        String newMnCurrBlockHash = HashUtil.hashSHA256(manufacturerCertificate.getManufacturer());
        manufacturerCertificate.getManufacturer().setMnCurrBlockHash(oldMnCurrBlockHash);
        manufacturerCertificate.getManufacturer().setUser(tempMnUser);
        if (newMnCurrBlockHash.equals(oldMnCurrBlockHash)) {
            if (newMnCurrBlockHash.equals(manufacturerCertificate.getMnCertPrevBlockHash())) {
                User tempMnCertUser = manufacturerCertificate.getManufacturer().getUser();
                String oldMnCertCurrBlockHash = manufacturerCertificate.getMnCertCurrBlockHash();
                manufacturerCertificate.setMnCertCurrBlockHash(null);
                manufacturerCertificate.getManufacturer().setUser(null);
                String newMnCertCurrBlockHash = HashUtil.hashSHA256(manufacturerCertificate);
                manufacturerCertificate.setMnCertCurrBlockHash(oldMnCertCurrBlockHash);
                manufacturerCertificate.getManufacturer().setUser(tempMnCertUser);
                if (newMnCertCurrBlockHash.equals(oldMnCertCurrBlockHash)) {

                } else {
                    incorrectData.put("10","MANUFACTURER CERTIFICATE CURRENT BLOCK HASH");
                    System.out.println("E10");
                    return incorrectData;
                }
            } else {
                incorrectData.put("9","MANUFACTURER CERTIFICATE PREVIOUS BLOCK HASH");
                System.out.println("E9");
                return incorrectData;
            }
        } else {
            incorrectData.put("8","MANUFACTURER CURRENT BLOCK HASH");
            System.out.println("E8");
            return incorrectData;
        }

        User tempMnUser2 = manufacturerCertificate.getManufacturer().getUser();
        String oldMnCurrBlockHash2 = product.getManufacturer().getMnCurrBlockHash();
        product.getManufacturer().setMnCurrBlockHash(null);
        product.getManufacturer().setUser(null);
        String newMnCurrBlockHash2 = HashUtil.hashSHA256(product.getManufacturer());
        product.getManufacturer().setMnCurrBlockHash(oldMnCurrBlockHash2);
        product.getManufacturer().setUser(tempMnUser2);
        if (newMnCurrBlockHash2.equals(oldMnCurrBlockHash2)) {
            System.out.println(newMnCurrBlockHash2);
            System.out.println(product.getPdPrevBlockHash());
            if (newMnCurrBlockHash2.equals(product.getPdPrevBlockHash())) {
                User tempPdUser = product.getManufacturer().getUser();
                String oldPdCurrBlockHash = product.getPdCurrBlockHash();
                product.setPdCurrBlockHash(null);
                product.getManufacturer().setUser(null);
                String newPdCurrBlockHash = HashUtil.hashSHA256(product);
                product.setPdCurrBlockHash(oldPdCurrBlockHash);
                product.getManufacturer().setUser(tempPdUser);
                if (newPdCurrBlockHash.equals(oldPdCurrBlockHash)) {

                } else {
                    incorrectData.put("13","PRODUCT CURRENT BLOCK HASH");
                    System.out.println("E13");
                    return incorrectData;
                }
            } else {
                incorrectData.put("12","PRODUCT PREVIOUS BLOCK HASH");
                System.out.println("E12");
                return incorrectData;
            }
        } else {
            incorrectData.put("11","MANUFACTURER CURRENT BLOCK HASH");
            System.out.println("E11");
            return incorrectData;
        }

        if (rawMaterialShipping.getRmsCurrBlockHash().equals(manufacturing.getManuftPrevBlockHash())) {
            User tempFmUserNew = manufacturing.getRawMaterialShipping().getPlanting().getFarmerCertificate().getFarmer().getUser();
            User tempMnUserNew = manufacturing.getProduct().getManufacturer().getUser();

            String oldManuftCurrBlockHash = manufacturing.getManuftCurrBlockHash();

            manufacturing.setManuftCurrBlockHash(null);

            manufacturing.getRawMaterialShipping().getPlanting().getFarmerCertificate().getFarmer().setUser(null);

            //Rms, manuftcert, product
            manufacturing.getProduct().getManufacturer().setUser(null);
            manufacturing.getRawMaterialShipping().getManufacturer().setUser(null);
            manufacturing.getManufacturerCertificate().getManufacturer().setUser(null);

            String newManuftCurrBlockHash = HashUtil.hashSHA256(manufacturing);

            manufacturing.setManuftCurrBlockHash(oldManuftCurrBlockHash);
            manufacturing.getRawMaterialShipping().getPlanting().getFarmerCertificate().getFarmer().setUser(tempFmUserNew);

            //Rms, manuftcert, product
            manufacturing.getProduct().getManufacturer().setUser(tempMnUserNew);
            manufacturing.getRawMaterialShipping().getManufacturer().setUser(tempMnUserNew);
            manufacturing.getManufacturerCertificate().getManufacturer().setUser(tempMnUserNew);

            if (newManuftCurrBlockHash.equals(oldManuftCurrBlockHash)) {
                return incorrectData;
            } else {
                incorrectData.put("15","MANUFACTURING CURRENT BLOCK HASH");
                System.out.println("E15");
                return incorrectData;
            }
        } else {
            incorrectData.put("14","MANUFACTURING PREVIOUS BLOCK HASH");
            System.out.println("E14");
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
