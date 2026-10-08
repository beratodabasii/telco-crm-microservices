package com.telcox.productcatalogservice.controller;

import com.telcox.productcatalogservice.dto.CreateTariffRequest;
import com.telcox.productcatalogservice.dto.TariffResponse;
import com.telcox.productcatalogservice.dto.UpdateTariffPriceRequest;
import com.telcox.productcatalogservice.service.TariffService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tariffs")
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @PostMapping
    public TariffResponse createTariff(
            @RequestBody CreateTariffRequest request
    ) {
        return tariffService.createTariff(request);
    }

    @GetMapping
    public List<TariffResponse> findAll() {
        return tariffService.getAllTariffs();
    }

    @GetMapping("/{code}")
    public TariffResponse findByCode(
            @PathVariable String code
    ) {
        return tariffService.getTariffByCode(code);
    }

    @PutMapping("/{code}/price")
    public TariffResponse updateTariffPrice(
            @PathVariable String code,
            @RequestBody UpdateTariffPriceRequest request
    ) {
        return tariffService.updateTariffPrice(
                code,
                request.getNewPrice()
        );
    }
}