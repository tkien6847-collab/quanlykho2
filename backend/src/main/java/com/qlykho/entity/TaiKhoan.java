package com.qlykho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Locale;

@Entity
@Table(name = "TAIKHOAN")
public class TaiKhoan {
    public static final String HOAT_DONG = "Hoạt động";
    public static final String NGUNG_HOAT_DONG = "Ngừng hoạt động";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTK")
    private Integer maTK;

    @Column(name = "TenDangNhap", nullable = false, unique = true, length = 50)
    private String tenDangNhap;

    @Column(name = "MatKhau", nullable = false, length = 255)
    private String matKhau;

    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai;

    @Column(name = "MaVaiTro", nullable = false)
    private Integer maVaiTro;

    @Column(name = "MaNV", nullable = false, unique = true)
    private Integer maNV;

    public TaiKhoan() {
    }

    public Integer getMaTK() { return maTK; }
    public void setMaTK(Integer maTK) { this.maTK = maTK; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public Integer getMaVaiTro() { return maVaiTro; }
    public void setMaVaiTro(Integer maVaiTro) { this.maVaiTro = maVaiTro; }
    public Integer getMaNV() { return maNV; }
    public void setMaNV(Integer maNV) { this.maNV = maNV; }

    public boolean dangHoatDong() {
        if (trangThai == null) return false;
        return switch (trangThai.trim().toLowerCase(Locale.ROOT)) {
            case "hoạt động", "dang hoat dong", "đang hoạt động", "active", "enabled" -> true;
            default -> false;
        };
    }

    public static boolean trangThaiHopLe(String trangThai) {
        return HOAT_DONG.equals(trangThai) || NGUNG_HOAT_DONG.equals(trangThai);
    }
}
