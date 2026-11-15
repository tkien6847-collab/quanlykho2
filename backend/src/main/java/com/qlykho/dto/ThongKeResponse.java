package com.qlykho.dto;

import java.time.LocalDate;

public record ThongKeResponse(
        LocalDate ngay,
        String thang,
        Long soLuongNhapTrongNgay,
        Long soLuongXuatTrongNgay,
        Long soLuongNhapTrongThang,
        Long soLuongXuatTrongThang
) {
}
