package com.qlykho.dto;

import com.qlykho.entity.ChiTietPhieuXuat;
import com.qlykho.entity.PhieuXuat;

import java.util.List;

public record PhieuXuatChiTietResponse(
        PhieuXuat phieuXuat,
        List<ChiTietPhieuXuat> chiTiet
) {
}
