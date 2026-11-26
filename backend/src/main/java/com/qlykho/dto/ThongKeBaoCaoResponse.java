package com.qlykho.dto;

import java.time.LocalDate;
import java.util.List;

public record ThongKeBaoCaoResponse(
        LocalDate tuNgay,
        LocalDate denNgay,
        Long soLuongNhap,
        Long soLuongXuat,
        List<ThongKeTheoDanhMuc> theoDanhMuc
) {
    public record ThongKeTheoDanhMuc(
            Integer maDM,
            String tenDM,
            Long soLuongNhap,
            Long soLuongXuat
    ) {}
}
