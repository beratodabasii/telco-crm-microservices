package com.telcox.productcatalogservice.controller;

import com.telcox.productcatalogservice.entity.Addon;
import com.telcox.productcatalogservice.entity.TariffAddon;
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
    public Addon createAddon(@RequestBody Addon addon) {
       return addonService.createAddon(addon);
    }


    @GetMapping
    public List<Addon> getAddonsByTariffCode(@RequestParam(required = false) String tariffCode) {
        if (tariffCode == null) {
            return addonService.getAllAddons();
        }
        return addonService.getAddonsByTariffCode(tariffCode);
    }

    @PostMapping("/{addonId}/tariffs/{tariffCode}")
    public TariffAddon addAddonToTariff(@PathVariable String tariffCode, @PathVariable Long addonId) {
        return addonService.addAddonToTariff(tariffCode, addonId);
    }

}
