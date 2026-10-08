package com.telcox.subscriptionservice.repository;

import com.telcox.subscriptionservice.entity.MsisdnPool;
import com.telcox.subscriptionservice.enums.MsisdnStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MsisdnPoolRepository extends JpaRepository<MsisdnPool, Integer> {

    Optional<MsisdnPool> findFirstByStatus(MsisdnStatus status);
}
