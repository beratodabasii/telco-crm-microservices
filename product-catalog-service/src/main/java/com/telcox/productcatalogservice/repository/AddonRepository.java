package com.telcox.productcatalogservice.repository;

import com.telcox.productcatalogservice.entity.Addon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddonRepository extends JpaRepository<Addon, Long> {
}
