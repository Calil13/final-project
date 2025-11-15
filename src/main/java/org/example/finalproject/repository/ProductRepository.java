package org.example.finalproject.repository;

import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Products, Long>, JpaSpecificationExecutor<Products> {

    Page<Products> findByVendorId(Long vendorId, Pageable pageable);
}
