package com.telcox.subscriptionservice.service;

import com.telcox.subscriptionservice.entity.MsisdnPool;
import com.telcox.subscriptionservice.enums.MsisdnStatus;
import com.telcox.subscriptionservice.repository.MsisdnPoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MsisdnPoolService {

    private final MsisdnPoolRepository msisdnPoolRepository;

    public MsisdnPool allocateMsisdn(){
        MsisdnPool msisdnPool = msisdnPoolRepository.findFirstByStatus(MsisdnStatus.FREE).orElseThrow(
                ()-> new RuntimeException("MsisdnPool not found")
        );
        msisdnPool.setStatus(MsisdnStatus.ALLOCATED);
        msisdnPool.setReservedUntil(null);
        return msisdnPoolRepository.save(msisdnPool);
    }
}
