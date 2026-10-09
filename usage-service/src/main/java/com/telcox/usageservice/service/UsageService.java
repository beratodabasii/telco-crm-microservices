package com.telcox.usageservice.service;

import com.telcox.usageservice.client.CatalogClient;
import com.telcox.usageservice.client.SubscriptionClient;
import com.telcox.usageservice.dto.*;
import com.telcox.usageservice.entity.Usage;
import com.telcox.usageservice.enums.UsageType;
import com.telcox.usageservice.repository.UsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


    public UsageResponse createUsage(CreateUsageRequest createUsageRequest) {

        Usage usage = new Usage();
        usage.setSubscriptionId(createUsageRequest.getSubscriptionId());
        usage.setAmount(createUsageRequest.getAmount());
        usage.setUsedAt(LocalDateTime.now());
        usage.setUsageType(createUsageRequest.getUsageType());
        Usage savedUsage = usageRepository.save(usage);
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

    private UsageResponse mapToResponse(Usage usage) {
        UsageResponse response = new UsageResponse();
        response.setId(usage.getId());
        response.setSubscriptionId(usage.getSubscriptionId());
        response.setUsageType(usage.getUsageType());
        response.setAmount(usage.getAmount());
        response.setUsedAt(usage.getUsedAt());

        return response;

    }


}
