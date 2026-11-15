package com.qlykho.repository;

import com.qlykho.entity.PhieuNhap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhieuNhapRepository extends JpaRepository<PhieuNhap, Integer> {
    boolean existsByMaNV(Integer maNV);
}
