package com.qlykho.dto;

public record DangNhapResponse(
        String accessToken,
        String tenDangNhap,
        Integer maVaiTro,
        String tenVaiTro,
        Integer maNV
) {
}
