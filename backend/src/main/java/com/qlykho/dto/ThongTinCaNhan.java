package com.qlykho.dto;

import java.time.LocalDate;

public record ThongTinCaNhan(
        String hoTen,
        String gioiTinh,
        LocalDate ngaySinh,
        String soDienThoai,
        String email,
        String diaChi
) {
}
