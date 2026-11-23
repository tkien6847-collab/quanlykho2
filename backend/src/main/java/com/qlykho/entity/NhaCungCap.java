package com.qlykho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "NHACUNGCAP")
public class NhaCungCap {
    public static final String HOAT_DONG = "Hoạt động";
    public static final String NGUNG_HOAT_DONG = "Ngừng hoạt động";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNCC")
    private Integer maNCC;

    @Column(name = "MaNCCCode", unique = true, length = 20)
    private String maNccCode;

    @Column(name = "TenNCC", nullable = false, length = 100)
    private String tenNCC;

    @Column(name = "SoDienThoai", nullable = false, unique = true, length = 20)
    private String soDienThoai;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "DiaChi", nullable = false, length = 200)
    private String diaChi;

    @Column(name = "TrangThai", nullable = false, length = 20)
    @ColumnDefault("'" + HOAT_DONG + "'")
    private String trangThai = HOAT_DONG;

    public NhaCungCap() {
    }

    public Integer getMaNCC() { return maNCC; }
    public void setMaNCC(Integer maNCC) { this.maNCC = maNCC; }
    public String getMaNccCode() { return maNccCode; }
    public void setMaNccCode(String maNccCode) { this.maNccCode = maNccCode; }
    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
