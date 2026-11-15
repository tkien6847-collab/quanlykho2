package com.qlykho.dto;

import java.time.LocalDate;
import java.util.List;

public record TaoPhieuXuatRequest(
        LocalDate ngayXuat,
        String ghiChu,
        Integer maNV,
        Integer maKH,
        String trangThai,
        List<ChiTietPhieuXuatRequest> chiTiet
) {
}
