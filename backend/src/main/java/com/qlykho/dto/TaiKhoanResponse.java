package com.qlykho.dto;

public record TaiKhoanResponse(
        Integer maTK,
        String tenDangNhap,
        String trangThai,
        Integer maVaiTro,
        Integer maNV,
        String hoTen,
        String tenVaiTro
) {
}
