package com.telcox.usageservice.controller;

import com.telcox.usageservice.dto.*;
import com.telcox.usageservice.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usages")
@RequiredArgsConstructor
public class UsageController {
    private final UsageService usageService;



    @PostMapping
    public UsageResponse createUsage(@RequestBody CreateUsageRequest createUsageRequest) {
        return usageService.createUsage(createUsageRequest);
    }

    @GetMapping("/subscriptions/{subscriptionId}")
    public List<UsageResponse> getUsagesBySubscriptionId(@PathVariable Long subscriptionId){
        return usageService.getUsagesBySubscriptionId(subscriptionId);
    }

    @GetMapping("/subscriptions/{subscriptionId}/summary")
    public UsageSummaryResponse getUsageSummary(@PathVariable Long subscriptionId){
        return usageService.getUsageSummary(subscriptionId);
    }

    @GetMapping("/subscriptions/{subscriptionId}/quota")
    public UsageQuotaResponse getUsageQuota(@PathVariable Long subscriptionId) {
        return usageService.getUsageQuota(subscriptionId);
    }

}
