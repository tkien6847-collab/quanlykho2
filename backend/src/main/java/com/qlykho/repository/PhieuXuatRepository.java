package com.qlykho.repository;

import com.qlykho.entity.PhieuXuat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhieuXuatRepository extends JpaRepository<PhieuXuat, Integer> {
    boolean existsByMaNV(Integer maNV);

    long countByTrangThaiIgnoreCase(String trangThai);
}
