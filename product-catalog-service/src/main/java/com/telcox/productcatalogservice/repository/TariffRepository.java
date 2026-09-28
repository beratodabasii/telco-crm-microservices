package com.telcox.productcatalogservice.repository;

import com.telcox.productcatalogservice.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Long> {

    Optional<Tariff> findTopByCodeOrderByVersionDesc(String code);
    @Query("""
       SELECT t
       FROM Tariff t
       WHERE t.version = (
           SELECT MAX(t2.version)
           FROM Tariff t2
           WHERE t2.code = t.code
       )
       """)
    List<Tariff> findAllLatestVersions();

}
