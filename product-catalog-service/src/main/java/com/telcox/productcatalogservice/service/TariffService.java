package com.telcox.productcatalogservice.service;

import com.telcox.productcatalogservice.entity.OutboxEvent;
import com.telcox.productcatalogservice.event.TariffCreatedEvent;
import com.telcox.productcatalogservice.event.TariffPriceChangedEvent;
import com.telcox.productcatalogservice.repository.OutboxEventRepository;
import com.telcox.productcatalogservice.repository.TariffRepository;
import com.telcox.productcatalogservice.entity.Tariff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TariffService {
    private final TariffRepository tariffRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    public TariffService(TariffRepository tariffRepository,
                         OutboxEventRepository outboxEventRepository,
                         ObjectMapper objectMapper) {
        this.tariffRepository = tariffRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }
    @Transactional
    public Tariff createTariff(Tariff tariff) {
        Tariff tariffEntity = new Tariff();
        tariffEntity.setCode(tariff.getCode());
        tariffEntity.setName(tariff.getName());
        tariffEntity.setTariffType(tariff.getTariffType());
        tariffEntity.setMonthlyFee(tariff.getMonthlyFee());
        tariffEntity.setMinutesIncluded(tariff.getMinutesIncluded());
        tariffEntity.setSmsIncluded(tariff.getSmsIncluded());
        tariffEntity.setDataMbIncluded(tariff.getDataMbIncluded());
        tariffEntity.setTariffStatus(tariff.getTariffStatus());
        tariffEntity.setEffectiveFrom(tariff.getEffectiveFrom());
        tariffEntity.setEffectiveTo(tariff.getEffectiveTo());
        tariffEntity.setVersion(1);
        Tariff savedTariff = tariffRepository.save(tariffEntity);
        TariffCreatedEvent event = new TariffCreatedEvent();
        event.setTariffId(savedTariff.getId());
        event.setCode(savedTariff.getCode());
        event.setName(savedTariff.getName());
        event.setTariffType(savedTariff.getTariffType());
        event.setMonthlyFee(savedTariff.getMonthlyFee());
        event.setMinutesIncluded(savedTariff.getMinutesIncluded());
        event.setSmsIncluded(savedTariff.getSmsIncluded());
        event.setDataMbIncluded(savedTariff.getDataMbIncluded());
        event.setTariffStatus(savedTariff.getTariffStatus());
        event.setEffectiveFrom(savedTariff.getEffectiveFrom());
        event.setEffectiveTo(savedTariff.getEffectiveTo());
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        OutboxEvent outboxEvent = new OutboxEvent();

        outboxEvent.setEventType(TariffCreatedEvent.class.getSimpleName());
        outboxEvent.setPublished(false);
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());

        outboxEventRepository.save(outboxEvent);


        return savedTariff;

    }

    public List<Tariff> getAllTariffs(){
        return tariffRepository.findAllLatestVersions();
    }

    public Tariff getTariffByCode(String code) {
        Tariff tariff = tariffRepository.findTopByCodeOrderByVersionDesc(code)
                .orElseThrow(() -> new RuntimeException("Tarife bulunamadı"));

        return tariff;
    }

    @Transactional
    public Tariff updateTariffPrice(String code, BigDecimal newPrice){
        Tariff currentTariff = tariffRepository.findTopByCodeOrderByVersionDesc(code)
                .orElseThrow(()-> new RuntimeException("Tariff with code " + code + " not found"));

        BigDecimal oldPrice = currentTariff.getMonthlyFee();
        Tariff newTariffVersion = new Tariff();
        newTariffVersion.setCode(currentTariff.getCode());
        newTariffVersion.setName(currentTariff.getName());
        newTariffVersion.setTariffType(currentTariff.getTariffType());
        newTariffVersion.setMinutesIncluded(currentTariff.getMinutesIncluded());
        newTariffVersion.setSmsIncluded(currentTariff.getSmsIncluded());
        newTariffVersion.setDataMbIncluded(currentTariff.getDataMbIncluded());
        newTariffVersion.setMonthlyFee(newPrice);
        newTariffVersion.setVersion(currentTariff.getVersion() + 1);
        newTariffVersion.setTariffStatus(currentTariff.getTariffStatus());
        newTariffVersion.setEffectiveFrom(currentTariff.getEffectiveFrom());
        newTariffVersion.setEffectiveTo(currentTariff.getEffectiveTo());
        Tariff savedTariff = tariffRepository.save(newTariffVersion);
        TariffPriceChangedEvent event = new TariffPriceChangedEvent();
        event.setTariffId(savedTariff.getId());
        event.setCode(savedTariff.getCode());
        event.setOldPrice(oldPrice);
        event.setNewPrice(newPrice);
        event.setChangedAt(LocalDateTime.now());
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(TariffPriceChangedEvent.class.getSimpleName());
        outboxEvent.setPublished(false);
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEventRepository.save(outboxEvent);
        return savedTariff;

    }


}
