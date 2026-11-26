package com.qlykho.dto;

import java.time.LocalDate;
import java.util.List;

public record ThongKeBieuDoResponse(
        String cheDo,
        List<MocThongKe> moc
) {
    public record MocThongKe(
            LocalDate ngay,
            String nhan,
            Long soLuongNhap,
            Long soLuongXuat,
            Long tonKhoLuyKe
    ) {}
}
