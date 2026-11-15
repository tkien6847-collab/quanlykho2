package com.qlykho.repository;

import com.qlykho.entity.ChiTietPhieuNhap;
import com.qlykho.entity.ChiTietPhieuNhapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChiTietPhieuNhapRepository extends JpaRepository<ChiTietPhieuNhap, ChiTietPhieuNhapId> {
    List<ChiTietPhieuNhap> findById_MaPN(Integer maPN);

    @Query("select sum(detail.soLuongNhap) from ChiTietPhieuNhap detail, PhieuNhap document " +
            "where detail.id.maPN = document.maPN and document.ngayNhap = :date")
    Long sumQuantityByDate(@Param("date") LocalDate date);

    @Query("select sum(detail.soLuongNhap) from ChiTietPhieuNhap detail, PhieuNhap document " +
            "where detail.id.maPN = document.maPN and document.ngayNhap between :startDate and :endDate")
    Long sumQuantityByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
