package com.qlykho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietPhieuXuatId implements Serializable {
    @Column(name = "MaPX")
    private Integer maPX;

    @Column(name = "MaHH")
    private Integer maHH;

    public ChiTietPhieuXuatId() {
    }

    public ChiTietPhieuXuatId(Integer maPX, Integer maHH) {
        this.maPX = maPX;
        this.maHH = maHH;
    }

    public Integer getMaPX() { return maPX; }
    public void setMaPX(Integer maPX) { this.maPX = maPX; }
    public Integer getMaHH() { return maHH; }
    public void setMaHH(Integer maHH) { this.maHH = maHH; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ChiTietPhieuXuatId that)) return false;
        return Objects.equals(maPX, that.maPX) && Objects.equals(maHH, that.maHH);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPX, maHH);
    }
}
