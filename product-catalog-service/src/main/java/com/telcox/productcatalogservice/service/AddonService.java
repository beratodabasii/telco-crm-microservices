package com.telcox.productcatalogservice.service;

import com.telcox.productcatalogservice.dto.AddonResponse;
import com.telcox.productcatalogservice.dto.CreateAddonRequest;
import com.telcox.productcatalogservice.dto.TariffAddonResponse;
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

    public AddonService(
            AddonRepository addonRepository,
            TariffAddonRepository tariffAddonRepository,
            TariffRepository tariffRepository
    ) {
        this.addonRepository = addonRepository;
        this.tariffAddonRepository = tariffAddonRepository;
        this.tariffRepository = tariffRepository;
    }

    public AddonResponse createAddon(CreateAddonRequest request) {

        Addon newAddon = new Addon();

        newAddon.setCode(request.getCode());
        newAddon.setName(request.getName());
        newAddon.setPrice(request.getPrice());
        newAddon.setAddonType(request.getAddonType());
        newAddon.setValidityDays(request.getValidityDays());

        Addon savedAddon =
                addonRepository.save(newAddon);

        return mapToResponse(savedAddon);
    }

    public List<AddonResponse> getAllAddons() {

        return addonRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AddonResponse> getAddonsByTariffCode(
            String tariffCode
    ) {

        Tariff tariff =
                tariffRepository
                        .findTopByCodeOrderByVersionDesc(tariffCode)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Tariff with code " +
                                                tariffCode +
                                                " not found"
                                )
                        );

        List<TariffAddon> tariffAddons =
                tariffAddonRepository.findByTariffId(
                        tariff.getId()
                );

        List<AddonResponse> result =
                new ArrayList<>();

        for (TariffAddon tariffAddon : tariffAddons) {

            Addon addon =
                    addonRepository.findById(
                                    tariffAddon.getAddonId()
                            )
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Addon with id " +
                                                    tariffAddon.getAddonId() +
                                                    " not found"
                                    )
                            );

            result.add(mapToResponse(addon));
        }

        return result;
    }

    public TariffAddonResponse addAddonToTariff(
            String tariffCode,
            Long addonId
    ) {

        Tariff tariff =
                tariffRepository
                        .findTopByCodeOrderByVersionDesc(tariffCode)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Tariff with code " +
                                                tariffCode +
                                                " not found"
                                )
                        );

        Addon addon =
                addonRepository.findById(addonId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Addon with id " +
                                                addonId +
                                                " not found"
                                )
                        );

        TariffAddon tariffAddon =
                new TariffAddon();

        tariffAddon.setAddonId(addon.getId());
        tariffAddon.setTariffId(tariff.getId());

        TariffAddon savedTariffAddon =
                tariffAddonRepository.save(tariffAddon);

        return mapTariffAddonToResponse(savedTariffAddon);
    }

    private AddonResponse mapToResponse(Addon addon) {

        AddonResponse response =
                new AddonResponse();

        response.setId(addon.getId());
        response.setCode(addon.getCode());
        response.setName(addon.getName());
        response.setPrice(addon.getPrice());
        response.setAddonType(addon.getAddonType());
        response.setValidityDays(addon.getValidityDays());

        return response;
    }

    private TariffAddonResponse mapTariffAddonToResponse(
            TariffAddon tariffAddon
    ) {

        TariffAddonResponse response =
                new TariffAddonResponse();

        response.setId(tariffAddon.getId());
        response.setTariffId(tariffAddon.getTariffId());
        response.setAddonId(tariffAddon.getAddonId());

        return response;
    }
}