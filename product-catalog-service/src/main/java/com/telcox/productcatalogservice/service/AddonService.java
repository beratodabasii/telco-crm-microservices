package com.telcox.productcatalogservice.service;

import com.telcox.productcatalogservice.entity.Addon;
import com.telcox.productcatalogservice.entity.Tariff;
import com.telcox.productcatalogservice.entity.TariffAddon;
import com.telcox.productcatalogservice.repository.AddonRepository;
import com.telcox.productcatalogservice.repository.TariffAddonRepository;
import com.telcox.productcatalogservice.repository.TariffRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddonService {
    private final AddonRepository addonRepository;
    private final TariffRepository tariffRepository;
    private final TariffAddonRepository tariffAddonRepository;
    public AddonService(AddonRepository addonRepository,
                        TariffAddonRepository tariffAddonRepository,
                        TariffRepository tariffRepository) {
        this.addonRepository = addonRepository;
        this.tariffAddonRepository = tariffAddonRepository;
        this.tariffRepository = tariffRepository;
    }

    public Addon createAddon(Addon addon) {
        Addon newAddon = new Addon();
        newAddon.setCode(addon.getCode());
        newAddon.setName(addon.getName());
        newAddon.setPrice(addon.getPrice());
        newAddon.setAddonType(addon.getAddonType());
        newAddon.setValidityDays(addon.getValidityDays());
        Addon savedAddon = addonRepository.save(newAddon);
        return savedAddon;
    }

    public List<Addon> getAllAddons() {
        return addonRepository.findAll();
    }

    public List<Addon> getAddonsByTariffCode(String tariffCode){
        Tariff tariff = tariffRepository.findTopByCodeOrderByVersionDesc(tariffCode).orElseThrow(
                ()-> new RuntimeException("Tariff with code " + tariffCode + " not found")
        );

        List<TariffAddon> tariffAddons = tariffAddonRepository.findByTariffId(tariff.getId());

        List<Addon> result = new ArrayList<>();
        for (TariffAddon tariffAddon : tariffAddons) {
           Addon addon = addonRepository.findById(tariffAddon.getAddonId()).orElseThrow(
                   ()-> new RuntimeException("Addon with id " + tariffAddon.getAddonId() + " not found")
           );
           result.add(addon);

        }
        return result;

    }
    public TariffAddon addAddonToTariff(String tariffCode, Long addonId){
        Tariff tariff = tariffRepository.findTopByCodeOrderByVersionDesc(tariffCode).orElseThrow(
                ()-> new RuntimeException("Tariff with code " + tariffCode + " not found")
        );
        Addon addon = addonRepository.findById(addonId).orElseThrow(
                ()-> new RuntimeException("Addon with id " + addonId + " not found")
        );
        TariffAddon tariffAddon = new TariffAddon();
        tariffAddon.setAddonId(addon.getId());
        tariffAddon.setTariffId(tariff.getId());
        TariffAddon savedTariffAddon = tariffAddonRepository.save(tariffAddon);
        return savedTariffAddon;



    }


}
