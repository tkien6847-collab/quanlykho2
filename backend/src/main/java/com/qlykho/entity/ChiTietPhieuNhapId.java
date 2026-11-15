package com.qlykho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietPhieuNhapId implements Serializable {
    @Column(name = "MaPN")
    private Integer maPN;

    @Column(name = "MaHH")
    private Integer maHH;

    public ChiTietPhieuNhapId() {
    }

    public ChiTietPhieuNhapId(Integer maPN, Integer maHH) {
        this.maPN = maPN;
        this.maHH = maHH;
    }

    public Integer getMaPN() { return maPN; }
    public void setMaPN(Integer maPN) { this.maPN = maPN; }
    public Integer getMaHH() { return maHH; }
    public void setMaHH(Integer maHH) { this.maHH = maHH; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ChiTietPhieuNhapId that)) return false;
        return Objects.equals(maPN, that.maPN) && Objects.equals(maHH, that.maHH);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPN, maHH);
    }
}
