package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.Manufacturer;
import org.itsci.mju_food_trace_ws.model.ManufacturerCertificate;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ManufacturerService {

    List<Manufacturer> getAllManufacturers();
    List<Manufacturer> getManufacturersByRegistStat();
    Manufacturer getManufacturerById(String manuftId);
    Manufacturer updateMnRegistStatus(String manuftId);
    Manufacturer declineMnRegistStatus(String manuftId);
    ManufacturerCertificate getManufacturerDetails(String manuftId);
    Manufacturer saveManufacturer(Map<String, String> map) throws ParseException;
    Manufacturer updateManufacturer(Manufacturer manufacturer);
    void deleteManufacturer(String manuftId);
    Manufacturer getManufacturerByUsername(String username);
}
