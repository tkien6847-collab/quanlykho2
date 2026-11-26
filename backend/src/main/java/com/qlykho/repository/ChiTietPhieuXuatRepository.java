package com.qlykho.repository;

import com.qlykho.entity.ChiTietPhieuXuat;
import com.qlykho.entity.ChiTietPhieuXuatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChiTietPhieuXuatRepository extends JpaRepository<ChiTietPhieuXuat, ChiTietPhieuXuatId> {
    List<ChiTietPhieuXuat> findById_MaPX(Integer maPX);

    @Query("select sum(detail.soLuongXuat) from ChiTietPhieuXuat detail, PhieuXuat document " +
            "where detail.id.maPX = document.maPX and document.ngayXuat = :date")
    Long sumQuantityByDate(@Param("date") LocalDate date);

    @Query("select sum(detail.soLuongXuat) from ChiTietPhieuXuat detail, PhieuXuat document " +
            "where detail.id.maPX = document.maPX and document.ngayXuat between :startDate and :endDate")
    Long sumQuantityByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("select detail.id.maHH, sum(detail.soLuongXuat) from ChiTietPhieuXuat detail, PhieuXuat document " +
            "where detail.id.maPX = document.maPX and document.ngayXuat between :startDate and :endDate " +
            "group by detail.id.maHH")
    List<Object[]> sumQuantityByProductAndDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("select document.ngayXuat, sum(detail.soLuongXuat) from ChiTietPhieuXuat detail, PhieuXuat document " +
            "where detail.id.maPX = document.maPX and document.ngayXuat between :startDate and :endDate " +
            "group by document.ngayXuat")
    List<Object[]> sumQuantityByEachDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("select sum(detail.soLuongXuat) from ChiTietPhieuXuat detail, PhieuXuat document " +
            "where detail.id.maPX = document.maPX and document.ngayXuat < :beforeDate")
    Long sumQuantityBeforeDate(@Param("beforeDate") LocalDate beforeDate);
}
