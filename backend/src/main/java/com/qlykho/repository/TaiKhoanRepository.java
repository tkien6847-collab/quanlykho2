package com.qlykho.repository;

import com.qlykho.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    boolean existsByTenDangNhap(String tenDangNhap);
    boolean existsByMaNV(Integer maNV);
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
}
