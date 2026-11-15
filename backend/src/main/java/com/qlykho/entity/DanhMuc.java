package com.qlykho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DANHMUC")
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDM")
    private Integer maDM;

    @Column(name = "TenDM", nullable = false, unique = true, length = 100)
    private String tenDM;

    @Column(name = "MoTa", length = 200)
    private String moTa;

    public DanhMuc() {
    }

    public Integer getMaDM() { return maDM; }
    public void setMaDM(Integer maDM) { this.maDM = maDM; }
    public String getTenDM() { return tenDM; }
    public void setTenDM(String tenDM) { this.tenDM = tenDM; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
