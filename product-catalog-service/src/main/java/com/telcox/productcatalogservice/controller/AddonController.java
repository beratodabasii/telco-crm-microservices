package com.telcox.productcatalogservice.controller;

import com.telcox.productcatalogservice.dto.AddonResponse;
import com.telcox.productcatalogservice.dto.CreateAddonRequest;
import com.telcox.productcatalogservice.dto.TariffAddonResponse;
import com.telcox.productcatalogservice.service.AddonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addons")
public class AddonController {

    private final AddonService addonService;

    public AddonController(AddonService addonService) {
        this.addonService = addonService;
    }

    @PostMapping
    public AddonResponse createAddon(
            @RequestBody CreateAddonRequest request
    ) {
        return addonService.createAddon(request);
    }

    @GetMapping
    public List<AddonResponse> getAddonsByTariffCode(
            @RequestParam(required = false) String tariffCode
    ) {

        if (tariffCode == null) {
            return addonService.getAllAddons();
        }

        return addonService.getAddonsByTariffCode(tariffCode);
    }

    @PostMapping("/{addonId}/tariffs/{tariffCode}")
    public TariffAddonResponse addAddonToTariff(
            @PathVariable String tariffCode,
            @PathVariable Long addonId
    ) {
        return addonService.addAddonToTariff(
                tariffCode,
                addonId
        );
    }
}