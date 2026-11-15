package com.qlykho.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CHITIETPHIEUNHAP")
public class ChiTietPhieuNhap {
    @EmbeddedId
    private ChiTietPhieuNhapId id;

    @Column(name = "SoLuongNhap", nullable = false)
    private Integer soLuongNhap;

    @Column(name = "DonGiaNhap", nullable = false, precision = 19, scale = 0)
    private BigDecimal donGiaNhap = BigDecimal.ZERO;

    @Column(name = "HSD")
    private LocalDate hanSuDung;

    public ChiTietPhieuNhap() {
    }

    public ChiTietPhieuNhapId getId() { return id; }
    public void setId(ChiTietPhieuNhapId id) { this.id = id; }
    public Integer getSoLuongNhap() { return soLuongNhap; }
    public void setSoLuongNhap(Integer soLuongNhap) { this.soLuongNhap = soLuongNhap; }
    public BigDecimal getDonGiaNhap() { return donGiaNhap; }
    public void setDonGiaNhap(BigDecimal donGiaNhap) { this.donGiaNhap = donGiaNhap; }
    public LocalDate getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(LocalDate hanSuDung) { this.hanSuDung = hanSuDung; }
}
