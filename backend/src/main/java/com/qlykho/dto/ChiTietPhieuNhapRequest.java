package com.qlykho.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChiTietPhieuNhapRequest(
        Integer maHH,
        Integer soLuongNhap,
        BigDecimal donGiaNhap,
        LocalDate hanSuDung
) {
}
