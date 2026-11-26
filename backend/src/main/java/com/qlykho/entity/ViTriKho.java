package com.qlykho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "VITRIKHO")
public class ViTriKho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaViTri")
    private Integer maViTri;

    @Column(name = "MaViTriCode", nullable = false, unique = true, length = 50)
    private String maViTriCode;

    @Column(name = "KhuVuc", nullable = false, length = 30)
    private String khuVuc;

    @Column(name = "MaKe", nullable = false, length = 20)
    private String maKe;

    @Column(name = "Tang", nullable = false)
    private Integer tang;

    @Column(name = "Oke", nullable = false)
    private Integer viTriO;

    @Column(name = "SucChua", nullable = false)
    private Integer sucChua = 50;

    public ViTriKho() {}
    public Integer getMaViTri() { return maViTri; }
    public void setMaViTri(Integer maViTri) { this.maViTri = maViTri; }
    public String getMaViTriCode() { return maViTriCode; }
    public void setMaViTriCode(String maViTriCode) { this.maViTriCode = maViTriCode; }
    public String getKhuVuc() { return khuVuc; }
    public void setKhuVuc(String khuVuc) { this.khuVuc = khuVuc; }
    public String getMaKe() { return maKe; }
    public void setMaKe(String maKe) { this.maKe = maKe; }
    public Integer getTang() { return tang; }
    public void setTang(Integer tang) { this.tang = tang; }
    public Integer getViTriO() { return viTriO; }
    public void setViTriO(Integer viTriO) { this.viTriO = viTriO; }
    public Integer getSucChua() { return sucChua; }
    public void setSucChua(Integer sucChua) { this.sucChua = sucChua; }
}
