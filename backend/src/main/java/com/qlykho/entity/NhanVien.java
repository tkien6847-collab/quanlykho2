package com.qlykho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "NHANVIEN")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNV")
    private Integer maNV;

    @Column(name = "HoTen", nullable = false, length = 50)
    private String hoTen;

    @Column(name = "GioiTinh", nullable = false, length = 10)
    private String gioiTinh;

    @Column(name = "NgaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "SoDienThoai", nullable = false, unique = true, length = 20)
    private String soDienThoai;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "DiaChi", nullable = false, length = 200)
    private String diaChi;

    @Column(name = "ChucVu", nullable = false, length = 50)
    private String chucVu;

    public NhanVien() {
    }

    public Integer getMaNV() { return maNV; }
    public void setMaNV(Integer maNV) { this.maNV = maNV; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
}
