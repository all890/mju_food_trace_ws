package org.itsci.mju_food_trace_ws.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.itsci.mju_food_trace_ws.model.Manufacturing;
import org.itsci.mju_food_trace_ws.model.QRCode;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        String maxQRCodeId = qrCodeRepository.getMaxQRCodeId();
        long maxQRCodeLong = 0;

        if (maxQRCodeId != null) {
            maxQRCodeLong = Long.parseLong(maxQRCodeId.substring(2));
        }

        String qrcodeId = generateQRCodeId(maxQRCodeLong + 1);

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
        while (result.length() < 8) {
            result = "0" + result;
        }
        result = "QR" + result;
        return result;
    }
}
