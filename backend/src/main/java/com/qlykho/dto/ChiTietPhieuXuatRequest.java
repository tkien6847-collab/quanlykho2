package com.qlykho.dto;

import java.math.BigDecimal;

public record ChiTietPhieuXuatRequest(
        Integer maHH,
        Integer soLuongXuat,
        BigDecimal donGiaXuat
) {
}
