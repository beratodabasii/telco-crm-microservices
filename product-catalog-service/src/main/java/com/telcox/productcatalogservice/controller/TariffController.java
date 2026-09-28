package com.telcox.productcatalogservice.controller;

import com.telcox.productcatalogservice.entity.Tariff;
import com.telcox.productcatalogservice.service.TariffService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tariffs")
public class TariffController {
    private final TariffService tariffService;
    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @PostMapping
    public Tariff createTariff(@RequestBody Tariff tariff) {
        return tariffService.createTariff(tariff);
    }

    @GetMapping
    public List<Tariff> findAll() {
        return tariffService.getAllTariffs();
    }

    @GetMapping("/{code}")
    public Tariff findByCode(@PathVariable String code) {
        return tariffService.getTariffByCode(code);
    }

    @PutMapping("/{code}/price")
    public Tariff updateTariffPrice(@PathVariable String code, @RequestBody BigDecimal newPrice) {
        return tariffService.updateTariffPrice(code, newPrice);
    }

}
