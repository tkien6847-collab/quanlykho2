package com.qlykho.repository;

import com.qlykho.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsByEmail(String email);
    boolean existsBySoDienThoaiAndMaNVNot(String soDienThoai, Integer maNV);
    boolean existsByEmailAndMaNVNot(String email, Integer maNV);
}
