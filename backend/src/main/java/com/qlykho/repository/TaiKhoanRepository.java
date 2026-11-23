package com.qlykho.repository;

import com.qlykho.entity.TaiKhoan;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    boolean existsByTenDangNhap(String tenDangNhap);
    boolean existsByMaNV(Integer maNV);
    Optional<TaiKhoan> findByMaNV(Integer maNV);
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from TaiKhoan account where account.maNV = :maNV")
    Optional<TaiKhoan> findByMaNVForUpdate(@Param("maNV") Integer maNV);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from TaiKhoan account where account.maTK = :maTK")
    Optional<TaiKhoan> findByMaTKForUpdate(@Param("maTK") Integer maTK);
}
