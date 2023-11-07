package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.NaturalId;
import org.itsci.mju_food_trace_ws.model.*;
import org.itsci.mju_food_trace_ws.repository.*;
import org.itsci.mju_food_trace_ws.utils.HashUtil;
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
public class ManufacturingServiceImpl implements ManufacturingService {

    @Autowired
    private ManufacturingRepository manufacturingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ManufacturerCertificateService manufacturerCertificateService;

    @Autowired
    private RawMaterialShippingRepository rawMaterialShippingRepository;

    @Autowired
    private ManufacturerCertificateRepository manufacturerCertificateRepository;

    @Override
    public List<Manufacturing> getAllManufacturing() {
        return manufacturingRepository.findAll();
    }

    @Override
    public List<Manufacturing> getManufacturingByProductId(String productId) {
        return manufacturingRepository.getManufacturingsByProduct_ProductId(productId);
    }

    @Override
    public Manufacturing getManufacturingById(String manufacturingId) {
        return manufacturingRepository.getReferenceById(manufacturingId);
    }

    @Override
    public Manufacturing addManufacturing(Map<String, String> map) throws ParseException {
        Manufacturing manufacturing = null;

        String maxManufacturingId = manufacturingRepository.getMaxManufacturingId();
        long maxManufacturingLong = 0;

        if (maxManufacturingId != null) {
            maxManufacturingLong = Long.parseLong(maxManufacturingId.substring(2));
        }
        //Planting session
        String manufacturingId = generateManufacturingId(maxManufacturingLong + 1);
        String manufactureDatestr = map.get("manufactureDate");
        String expireDatestr = map.get("expireDate");

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date manufactureDate = format.parse(manufactureDatestr);
        Date expireDate = format.parse(expireDatestr);

        int productQty = Integer.parseInt(map.get("productQty"));
        String productUnit = map.get("productUnit");
        double usedRawMatQty = Double.parseDouble(map.get("usedRawMatQty"));
        String usedRawMatQtyUnit = map.get("usedRawMatQtyUnit");
        String rawMaterialShippingId = map.get("rawMaterialShippingId");
        RawMaterialShipping rawMaterialShipping = rawMaterialShippingRepository.getReferenceById(rawMaterialShippingId);

        String manuftPrevBlockHash = rawMaterialShipping.getRmsCurrBlockHash();

        String productId = map.get("productId");
        System.out.println("productid is :"+productId);
        Product product = productRepository.getReferenceById(productId);

        ManufacturerCertificate manufacturerCertificate = manufacturerCertificateService.
                getLatestManufacturerCertificateByManufacturerUsername(rawMaterialShipping.getManufacturer().getUser().getUsername());

        manufacturing = new Manufacturing(manufacturingId,manufactureDate,expireDate,productQty,productUnit,usedRawMatQty,usedRawMatQtyUnit,
                manuftPrevBlockHash, null,rawMaterialShipping,product, manufacturerCertificate);



        return manufacturingRepository.save(manufacturing);
    }

    @Override
    public Manufacturing updateManufacturing(Manufacturing manufacturing) {
        return manufacturingRepository.save(manufacturing);
    }

    @Override
    public void deleteManufacturing(String manufacturingId) {
        Manufacturing manufacturing = manufacturingRepository.getReferenceById(manufacturingId);
        manufacturing.setRawMaterialShipping(null);
        manufacturing.setProduct(null);
        manufacturingRepository.delete(manufacturing);
    }

    @Override
    public Manufacturing recordManufacturing(String manufacturingId) throws JsonProcessingException, NoSuchAlgorithmException {
        Manufacturing manufacturing = manufacturingRepository.getReferenceById(manufacturingId);

        User tempFmUser = manufacturing.getRawMaterialShipping().getPlanting().getFarmerCertificate().getFarmer().getUser();
        User tempMnUser = manufacturing.getProduct().getManufacturer().getUser();

        manufacturing.getRawMaterialShipping().getPlanting().getFarmerCertificate().getFarmer().setUser(null);

        //Rms, manuftcert, product
        manufacturing.getProduct().getManufacturer().setUser(null);
        manufacturing.getRawMaterialShipping().getManufacturer().setUser(null);
        manufacturing.getManufacturerCertificate().getManufacturer().setUser(null);

        String jsonStr3 = new ObjectMapper().writeValueAsString(manufacturing);
        MessageDigest digest3 = MessageDigest.getInstance("SHA-256");
        byte[] hash3 = digest3.digest(jsonStr3.getBytes(StandardCharsets.UTF_8));
        String encodedManuftCurrBlockHash = Base64.getEncoder().encodeToString(hash3);

        manufacturing.setManuftCurrBlockHash(encodedManuftCurrBlockHash);

        manufacturing.getRawMaterialShipping().getPlanting().getFarmerCertificate().getFarmer().setUser(tempFmUser);

        //Rms, manuftcert, product
        manufacturing.getProduct().getManufacturer().setUser(tempMnUser);
        manufacturing.getRawMaterialShipping().getManufacturer().setUser(tempMnUser);
        manufacturing.getManufacturerCertificate().getManufacturer().setUser(tempMnUser);

        return manufacturingRepository.save(manufacturing);

        /*
        //First step : checking the current hash of planting
        String tempPtCurrHash = planting.getPtCurrBlockHash();
        planting.setPtCurrBlockHash(null);

        String jsonStr = new ObjectMapper().writeValueAsString(planting);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
        String encodedPtCurrBlockHash = Base64.getEncoder().encodeToString(hash);

        System.out.println("1 OLD HASH : " + tempPtCurrHash);
        System.out.println("1 NEW HASH : " + encodedPtCurrBlockHash);

        if (tempPtCurrHash.equals(encodedPtCurrBlockHash)) {
            if (rawMaterialShipping.getRmsPrevBlockHash().equals(tempPtCurrHash)) {
                planting.setPtCurrBlockHash(tempPtCurrHash);

                String tempRmsCurrHash = rawMaterialShipping.getRmsCurrBlockHash();
                rawMaterialShipping.setRmsCurrBlockHash(null);

                String jsonStr2 = new ObjectMapper().writeValueAsString(rawMaterialShipping);
                MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
                byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
                String encodedPtCurrBlockHash2 = Base64.getEncoder().encodeToString(hash2);

                System.out.println("3 OLD HASH : " + tempRmsCurrHash);
                System.out.println("3 NEW HASH : " + encodedPtCurrBlockHash2);

                if (tempRmsCurrHash.equals(encodedPtCurrBlockHash2)) {
                    if (manufacturing.getManuftPrevBlockHash().equals(tempRmsCurrHash)) {

                        rawMaterialShipping.setRmsCurrBlockHash(tempRmsCurrHash);


                    } else {
                        System.out.println("ERROR FOURTH FLOOR!");
                        return null;
                    }
                } else {
                    System.out.println("ERROR THIRD FLOOR!");
                    return null;
                }
            } else {
                System.out.println("ERROR SECOND FLOOR!");
                return null;
            }
        } else {
            System.out.println("ERROR FIRST FLOOR!");
            return null;
        }
        */
    }


    @Override
    public List<Manufacturing> getListAllManufacturingByUsername(String username) {
        return manufacturingRepository.getManufacturingsByProduct_Manufacturer_User_Username(username);
    }

    @Override
    public boolean isChainValidBeforeManufacturing(Map<String, String> map) throws NoSuchAlgorithmException, JsonProcessingException {

        String rawMatShpId = map.get("rawMatShpId");
        String productId = map.get("productId");

        RawMaterialShipping rawMaterialShipping = rawMaterialShippingRepository.getReferenceById(rawMatShpId);
        Product product = productRepository.getReferenceById(productId);

        System.out.println("FIRST : " + product.getPdPrevBlockHash() + " " + product.getProductId());

        ManufacturerCertificate manufacturerCertificate = manufacturerCertificateRepository.
                getLatestManufacturerCertificateByManufacturerUsername(product.getManufacturer().getUser().getUsername(), "อนุมัติ");

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
                                    System.out.println("E7");
                                    return false;
                                }
                            } else {
                                System.out.println("E6");
                                return false;
                            }
                        } else {
                            System.out.println("E5");
                            return false;
                        }
                    } else {
                        System.out.println("E4");
                        return false;
                    }
                } else {
                    System.out.println("E3");
                    return false;
                }
            } else {
                System.out.println("E2");
                return false;
            }
        } else {
            System.out.println("E1");
            return false;
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
                    System.out.println("E10");
                    return false;
                }
            } else {
                System.out.println("E9");
                return false;
            }
        } else {
            System.out.println("E8");
            return false;
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
                    System.out.println("E13");
                    return false;
                }
            } else {
                System.out.println("E12");
                return false;
            }
        } else {
            System.out.println("E11");
            return false;
        }

        return true;
    }

    @Override
    public boolean isChainValidBeforeRecordManufacturing(String manufacturingId) throws NoSuchAlgorithmException, JsonProcessingException {

        Manufacturing manufacturing = manufacturingRepository.getReferenceById(manufacturingId);

        RawMaterialShipping rawMaterialShipping = manufacturing.getRawMaterialShipping();
        ManufacturerCertificate manufacturerCertificate = manufacturing.getManufacturerCertificate();
        Product product = manufacturing.getProduct();

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
                                    if (newRmsCurrBlockHash.equals(manufacturing.getManuftPrevBlockHash())) {

                                    } else {
                                        System.out.println("E8");
                                        return false;
                                    }
                                } else {
                                    System.out.println("E7");
                                    return false;
                                }
                            } else {
                                System.out.println("E6");
                                return false;
                            }
                        } else {
                            System.out.println("E5");
                            return false;
                        }
                    } else {
                        System.out.println("E4");
                        return false;
                    }
                } else {
                    System.out.println("E3");
                    return false;
                }
            } else {
                System.out.println("E2");
                return false;
            }
        } else {
            System.out.println("E1");
            return false;
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
                    System.out.println("E11");
                    return false;
                }
            } else {
                System.out.println("E10");
                return false;
            }
        } else {
            System.out.println("E9");
            return false;
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
                    System.out.println("E14");
                    return false;
                }
            } else {
                System.out.println("E13");
                return false;
            }
        } else {
            System.out.println("E12");
            return false;
        }



        return true;
    }

    public String generateManufacturingId (long rawId) {
        String result = Long.toString(rawId);
        while (result.length() < 8) {
            result = "0" + result;
        }
        result = "MF" + result;
        return result;
    }
}
