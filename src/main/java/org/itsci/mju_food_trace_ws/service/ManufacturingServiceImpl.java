package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.Manufacturing;
import org.itsci.mju_food_trace_ws.repository.ManufacturingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ManufacturingServiceImpl implements ManufacturingService {

    @Autowired
    private ManufacturingRepository manufacturingRepository;

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
    public Manufacturing addManufacturing(Map<String, String> map) {
        return null;
    }

    @Override
    public Manufacturing updateManufacturing(Manufacturing manufacturing) {
        return manufacturingRepository.save(manufacturing);
    }

    @Override
    public void deleteManufacturing(String manufacturingId) {
        Manufacturing manufacturing = manufacturingRepository.getReferenceById(manufacturingId);
        manufacturingRepository.delete(manufacturing);
    }
}
