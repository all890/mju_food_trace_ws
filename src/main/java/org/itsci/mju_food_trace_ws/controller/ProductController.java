package org.itsci.mju_food_trace_ws.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itsci.mju_food_trace_ws.model.Product;
import org.itsci.mju_food_trace_ws.service.ManufacturingService;
import org.itsci.mju_food_trace_ws.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ManufacturingService manufacturingService;

    @RequestMapping("/add")
    public ResponseEntity addProduct (@RequestBody Map<String, String> map) {
        try {
            Product product = productService.addProduct(map);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save product data!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity updateProduct (@RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update product data!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getprodbyid/{productId}")
    public ResponseEntity getProductById (@PathVariable("productId") String productId) {
        try {
            Product product = productService.getProductById(productId);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get product details!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/delete/{productId}")
    public ResponseEntity deleteProduct (@PathVariable("productId") String productId) {
        try {
            if (manufacturingService.getManufacturingByProductId(productId).size() >= 1) {
                return new ResponseEntity<>("Cannot delete because this product was manufactured.", HttpStatus.CONFLICT);
            } else {
                productService.deleteProduct(productId);
                return new ResponseEntity<>("Product data was deleted successfully!", HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to delete product data!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getlistprodbyusername/{username}")
    public ResponseEntity getListProductByUsername (@PathVariable("username") String username) {
        try {
            List<Product> products = productService.getProductsByUsername(username);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list products!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getprodexists/{username}")
    public ResponseEntity getProductExistingByManufacturerUsername (@PathVariable("username") String username) {
        try {
            Map<String, String> prodExisting = productService.getProductExistingByManufacturerUsername(username);
            return new ResponseEntity<>(prodExisting, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list of remaining qty of plantings.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/testjson")
    public void testJson () throws JsonProcessingException, NoSuchAlgorithmException {
        Product product = productService.getProductById("PD00000001");
        String jsonStr = new ObjectMapper().writeValueAsString(product);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash);
        System.out.println(encoded);
    }

    @GetMapping("/getnewpdcurrblockhash/{productId}")
    public ResponseEntity getNewManuftCurrBlockHash (@PathVariable("productId") String productId) {
        try {
            String newPdCurrBlockHash = productService.getNewPdCurrBlockHash(productId);
            return new ResponseEntity<>(newPdCurrBlockHash, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get new fm curr block hash.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
