package com.qlykho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;

@Entity
@Table(name = "HANGHOA")
public class HangHoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHH")
    private Integer maHH;

    @Column(name = "TenHH", nullable = false, length = 100)
    private String tenHH;

    @Column(name = "DonViTinh", nullable = false, length = 20)
    private String donViTinh;

    @Column(name = "DonGia", nullable = false, precision = 19, scale = 0)
    private BigDecimal donGia = BigDecimal.ZERO;

    @Column(name = "SoLuongTon", nullable = false)
    private Integer soLuongTon = 0;

    @Column(name = "ViTriKe", length = 50)
    private String viTriKe;

    @Column(name = "HinhAnh", length = 255)
    private String hinhAnh;

    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai;

    @Column(name = "MaDM", nullable = false)
    private Integer maDM;

    @Transient
    private String tenDM;

    public HangHoa() {
    }

    public Integer getMaHH() { return maHH; }
    public void setMaHH(Integer maHH) { this.maHH = maHH; }
    public String getTenHH() { return tenHH; }
    public void setTenHH(String tenHH) { this.tenHH = tenHH; }
    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }
    public String getViTriKe() { return viTriKe; }
    public void setViTriKe(String viTriKe) { this.viTriKe = viTriKe; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public Integer getMaDM() { return maDM; }
    public void setMaDM(Integer maDM) { this.maDM = maDM; }
    public String getTenDM() { return tenDM; }
    public void setTenDM(String tenDM) { this.tenDM = tenDM; }
}
