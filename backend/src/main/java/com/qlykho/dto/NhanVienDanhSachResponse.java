package com.qlykho.dto;

import java.time.LocalDate;

public record NhanVienDanhSachResponse(
        Integer maNV,
        String hoTen,
        String gioiTinh,
        LocalDate ngaySinh,
        String soDienThoai,
        String email,
        String diaChi,
        String chucVu,
        String tenDangNhap,
        String trangThai
) {
}
