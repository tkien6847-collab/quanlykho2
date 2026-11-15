package com.qlykho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PHIEUNHAP")
public class PhieuNhap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaPN")
    private Integer maPN;

    @Column(name = "NgayNhap", nullable = false)
    private LocalDate ngayNhap;

    @Column(name = "TongTien", nullable = false, precision = 19, scale = 0)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(name = "GhiChu", length = 200)
    private String ghiChu;

    @Column(name = "MaNV", nullable = false)
    private Integer maNV;

    @Column(name = "MaNCC", nullable = false)
    private Integer maNCC;

    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai;

    public PhieuNhap() {
    }

    public Integer getMaPN() { return maPN; }
    public void setMaPN(Integer maPN) { this.maPN = maPN; }
    public LocalDate getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(LocalDate ngayNhap) { this.ngayNhap = ngayNhap; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public Integer getMaNV() { return maNV; }
    public void setMaNV(Integer maNV) { this.maNV = maNV; }
    public Integer getMaNCC() { return maNCC; }
    public void setMaNCC(Integer maNCC) { this.maNCC = maNCC; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
