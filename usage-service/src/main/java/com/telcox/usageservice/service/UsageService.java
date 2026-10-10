package com.telcox.usageservice.service;

import com.telcox.usageservice.client.CatalogClient;
import com.telcox.usageservice.client.SubscriptionClient;
import com.telcox.usageservice.dto.*;
import com.telcox.usageservice.entity.OutboxEvent;
import com.telcox.usageservice.entity.Usage;
import com.telcox.usageservice.entity.UsageThresholdRecord;
import com.telcox.usageservice.enums.UsageThresholdLevel;
import com.telcox.usageservice.enums.UsageType;
import com.telcox.usageservice.event.UsageThresholdReachedEvent;
import com.telcox.usageservice.repository.OutboxEventRepository;
import com.telcox.usageservice.repository.UsageRepository;
import com.telcox.usageservice.repository.UsageThresholdRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsageService {

    private final UsageRepository usageRepository;
    private final SubscriptionClient subscriptionClient;
    private final CatalogClient catalogClient;
    private final UsageThresholdRecordRepository usageThresholdRecordRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public UsageResponse createUsage(CreateUsageRequest createUsageRequest) {

        Usage usage = new Usage();
        usage.setSubscriptionId(createUsageRequest.getSubscriptionId());
        usage.setAmount(createUsageRequest.getAmount());
        usage.setUsedAt(LocalDateTime.now());
        usage.setUsageType(createUsageRequest.getUsageType());
        Usage savedUsage = usageRepository.save(usage);
        UsageQuotaResponse quota = getUsageQuota(savedUsage.getSubscriptionId());
        BigDecimal percentage = getPercentageForUsageType(savedUsage.getUsageType(), quota);
        UsageThresholdLevel thresholdLevel = getThresholdLevel(percentage);
        if (thresholdLevel != UsageThresholdLevel.NORMAL) {

            boolean alreadyExists =
                    usageThresholdRecordRepository
                            .existsBySubscriptionIdAndUsageTypeAndThresholdLevel(
                                    savedUsage.getSubscriptionId(),
                                    savedUsage.getUsageType(),
                                    thresholdLevel
                            );

            if (!alreadyExists) {

                UsageThresholdReachedEvent event =
                        new UsageThresholdReachedEvent(
                                savedUsage.getSubscriptionId(),
                                savedUsage.getUsageType(),
                                thresholdLevel,
                                percentage,
                                LocalDateTime.now()
                        );
                OutboxEvent outboxEvent = new OutboxEvent();

                outboxEvent.setAggregateType("SUBSCRIPTION");
                outboxEvent.setAggregateId(savedUsage.getSubscriptionId());
                outboxEvent.setEventType("UsageThresholdReached");
                outboxEvent.setPayload(toJson(event));
                outboxEvent.setCreatedAt(LocalDateTime.now());
                outboxEvent.setPublished(false);

                outboxEventRepository.save(outboxEvent);
                System.out.println(
                        "Threshold event created: "
                                + event.getSubscriptionId()
                                + " "
                                + event.getUsageType()
                                + " "
                                + event.getThresholdLevel()
                                + " "
                                + event.getPercentage()
                );
                UsageThresholdRecord record = new UsageThresholdRecord();

                record.setSubscriptionId(savedUsage.getSubscriptionId());
                record.setUsageType(savedUsage.getUsageType());
                record.setThresholdLevel(thresholdLevel);
                record.setCreatedAt(LocalDateTime.now());

                usageThresholdRecordRepository.save(record);
            }
        }

        return mapToResponse(savedUsage);

    }

    public List<UsageResponse> getUsagesBySubscriptionId(Long subscriptionId) {
        List<Usage> usages = usageRepository.findBySubscriptionIdOrderByUsedAtDesc(subscriptionId);
        return usages.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UsageSummaryResponse getUsageSummary(Long subscriptionId){
        List<Usage> usages = usageRepository.findBySubscriptionIdOrderByUsedAtDesc(subscriptionId);
        BigDecimal dataUsed = usages.stream()
                .filter(usage -> usage.getUsageType() ==  UsageType.DATA)
                .map(Usage::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal voiceUsed = usages.stream()
                .filter(usage -> usage.getUsageType() == UsageType.VOICE)
                .map(Usage::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal smsUsed = usages.stream()
                .filter(usage -> usage.getUsageType() == UsageType.SMS)
                .map(Usage::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        UsageSummaryResponse usageSummaryResponse = new UsageSummaryResponse();
        usageSummaryResponse.setSubscriptionId(subscriptionId);
        usageSummaryResponse.setDataUsed(dataUsed);
        usageSummaryResponse.setVoiceUsed(voiceUsed);
        usageSummaryResponse.setSmsUsed(smsUsed);
        return usageSummaryResponse;

    }

    public UsageQuotaResponse getUsageQuota(Long subscriptionId) {

        SubscriptionClientResponse subscriptionClientResponse =
                subscriptionClient.getSubscriptionById(subscriptionId);

        CatalogClientResponse catalogClientResponse =
                catalogClient.getTariffByCode(subscriptionClientResponse.getTariffCode());

        UsageSummaryResponse usageSummaryResponse =
                getUsageSummary(subscriptionId);

        BigDecimal dataPercentage =
                usageSummaryResponse.getDataUsed()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(
                                BigDecimal.valueOf(catalogClientResponse.getDataMbIncluded()),
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal voicePercentage =
                usageSummaryResponse.getVoiceUsed()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(
                                BigDecimal.valueOf(catalogClientResponse.getMinutesIncluded()),
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal smsPercentage =
                usageSummaryResponse.getSmsUsed()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(
                                BigDecimal.valueOf(catalogClientResponse.getSmsIncluded()),
                                2,
                                RoundingMode.HALF_UP
                        );

        UsageQuotaResponse usageQuotaResponse = new UsageQuotaResponse();

        usageQuotaResponse.setSubscriptionId(subscriptionId);

        usageQuotaResponse.setDataUsed(usageSummaryResponse.getDataUsed());
        usageQuotaResponse.setDataLimit(catalogClientResponse.getDataMbIncluded());
        usageQuotaResponse.setDataPercentage(dataPercentage);

        usageQuotaResponse.setVoiceUsed(usageSummaryResponse.getVoiceUsed());
        usageQuotaResponse.setVoiceLimit(catalogClientResponse.getMinutesIncluded());
        usageQuotaResponse.setVoicePercentage(voicePercentage);

        usageQuotaResponse.setSmsUsed(usageSummaryResponse.getSmsUsed());
        usageQuotaResponse.setSmsLimit(catalogClientResponse.getSmsIncluded());
        usageQuotaResponse.setSmsPercentage(smsPercentage);

        return usageQuotaResponse;
    }

    public UsageThresholdLevel checkThreshold(Long subscriptionId){
        UsageQuotaResponse quota = getUsageQuota(subscriptionId);

        if (quota.getDataPercentage().compareTo(BigDecimal.valueOf(100)) >= 0
                || quota.getVoicePercentage().compareTo(BigDecimal.valueOf(100)) >= 0
                || quota.getSmsPercentage().compareTo(BigDecimal.valueOf(100)) >= 0) {

            return UsageThresholdLevel.HUNDRED_PERCENT;
        }

        if (quota.getDataPercentage().compareTo(BigDecimal.valueOf(80)) >= 0
                || quota.getVoicePercentage().compareTo(BigDecimal.valueOf(80)) >= 0
                || quota.getSmsPercentage().compareTo(BigDecimal.valueOf(80)) >= 0) {

            return UsageThresholdLevel.EIGHTY_PERCENT;
        }


        return UsageThresholdLevel.NORMAL;
    }

    private UsageResponse mapToResponse(Usage usage) {
        UsageResponse response = new UsageResponse();
        response.setId(usage.getId());
        response.setSubscriptionId(usage.getSubscriptionId());
        response.setUsageType(usage.getUsageType());
        response.setAmount(usage.getAmount());
        response.setUsedAt(usage.getUsedAt());

        return response;

    }

    private UsageThresholdLevel  getThresholdLevel(BigDecimal percentage ) {
        if(percentage.compareTo(BigDecimal.valueOf(100)) >= 0){
            return UsageThresholdLevel.HUNDRED_PERCENT;
        }
        if(percentage.compareTo(BigDecimal.valueOf(80)) >= 0){
            return UsageThresholdLevel.EIGHTY_PERCENT;
        }
        return UsageThresholdLevel.NORMAL;

    }

    private BigDecimal getPercentageForUsageType(UsageType usageType, UsageQuotaResponse quota) {
        return switch (usageType) {
            case DATA -> quota.getDataPercentage();
            case VOICE -> quota.getVoicePercentage();
            case SMS -> quota.getSmsPercentage();
        };


    }
    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new RuntimeException("Event JSON'a çevrilemedi", e);
        }
    }

}
