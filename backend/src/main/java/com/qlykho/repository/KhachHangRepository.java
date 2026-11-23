package com.qlykho.repository;

import com.qlykho.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    List<KhachHang> findAllByTrangThaiOrderByHoTenAscMaKHAsc(String trangThai);
}
