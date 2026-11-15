package com.qlykho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "CHITIETPHIEUXUAT")
public class ChiTietPhieuXuat {
    @EmbeddedId
    private ChiTietPhieuXuatId id;

    @Column(name = "SoLuongXuat", nullable = false)
    private Integer soLuongXuat;

    @Column(name = "DonGiaXuat", nullable = false, precision = 19, scale = 0)
    private BigDecimal donGiaXuat = BigDecimal.ZERO;

    public ChiTietPhieuXuat() {
    }

    public ChiTietPhieuXuatId getId() { return id; }
    public void setId(ChiTietPhieuXuatId id) { this.id = id; }
    public Integer getSoLuongXuat() { return soLuongXuat; }
    public void setSoLuongXuat(Integer soLuongXuat) { this.soLuongXuat = soLuongXuat; }
    public BigDecimal getDonGiaXuat() { return donGiaXuat; }
    public void setDonGiaXuat(BigDecimal donGiaXuat) { this.donGiaXuat = donGiaXuat; }
}
