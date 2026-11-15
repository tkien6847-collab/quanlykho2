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
@Table(name = "PHIEUXUAT")
public class PhieuXuat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaPX")
    private Integer maPX;

    @Column(name = "NgayXuat", nullable = false)
    private LocalDate ngayXuat;

    @Column(name = "TongTien", nullable = false, precision = 18, scale = 0)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(name = "GhiChu", length = 200)
    private String ghiChu;

    @Column(name = "MaNV", nullable = false)
    private Integer maNV;

    @Column(name = "MaKH", nullable = false)
    private Integer maKH;

    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai;

    public PhieuXuat() {
    }

    public Integer getMaPX() { return maPX; }
    public void setMaPX(Integer maPX) { this.maPX = maPX; }
    public LocalDate getNgayXuat() { return ngayXuat; }
    public void setNgayXuat(LocalDate ngayXuat) { this.ngayXuat = ngayXuat; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public Integer getMaNV() { return maNV; }
    public void setMaNV(Integer maNV) { this.maNV = maNV; }
    public Integer getMaKH() { return maKH; }
    public void setMaKH(Integer maKH) { this.maKH = maKH; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
