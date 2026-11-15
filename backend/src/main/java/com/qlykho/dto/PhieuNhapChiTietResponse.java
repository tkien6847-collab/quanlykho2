package com.qlykho.dto;

import com.qlykho.entity.ChiTietPhieuNhap;
import com.qlykho.entity.PhieuNhap;

import java.util.List;

public record PhieuNhapChiTietResponse(
        PhieuNhap phieuNhap,
        List<ChiTietPhieuNhap> chiTiet
) {
}
