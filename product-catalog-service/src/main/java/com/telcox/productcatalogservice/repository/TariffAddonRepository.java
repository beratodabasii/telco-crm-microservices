package com.telcox.productcatalogservice.repository;

import com.telcox.productcatalogservice.entity.TariffAddon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TariffAddonRepository extends JpaRepository<TariffAddon, Long> {
    List<TariffAddon> findByTariffId(Long tariffId);
}
