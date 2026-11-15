package com.qlykho.dto;

import com.qlykho.entity.NhanVien;

public record TaoTaiKhoanRequest(
        NhanVien nhanVien,
        String tenDangNhap,
        String matKhau,
        Integer maVaiTro,
        String trangThai
) {
}
