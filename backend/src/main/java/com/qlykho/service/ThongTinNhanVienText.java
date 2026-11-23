package com.qlykho.service;

import com.qlykho.dto.ThongTinCaNhan;
import com.qlykho.entity.NhanVien;
import com.qlykho.security.AccountRole;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

final class ThongTinNhanVienText {
    private static final List<String> DEFAULT_NAMES = List.of("Quản trị viên", "Quản lý", "Nhân viên");
    private static final LocalDate STAFF_DEFAULT_BIRTHDAY = LocalDate.of(2000, 1, 1);
    private static final LocalDate ADMIN_DEFAULT_BIRTHDAY = LocalDate.of(1990, 1, 1);
    private static final String ADMIN_DEFAULT_PHONE = "0900000001";
    private static final Pattern GENERATED_EMAIL = Pattern.compile(
            "(?:manager|employee)-[0-9a-f]{8}(?:-[0-9a-f]{4}){3}-[0-9a-f]{12}@qlykho\\.local",
            Pattern.CASE_INSENSITIVE
    );

    private ThongTinNhanVienText() {
    }

    static String hoTenHienThi(String storedName, String username) {
        String name = suaMaHoaCu(storedName);
        if (name.indexOf('�') >= 0 || name.indexOf('?') >= 0) {
            return "";
        }
        for (String defaultName : DEFAULT_NAMES) {
            // Older accounts used the role followed by the username as a placeholder.
            String generatedName = defaultName + " - " + username;
            if (name.equals(defaultName) || name.equals(generatedName.substring(0, Math.min(50, generatedName.length())))) {
                return "";
            }
        }
        return name;
    }

    static ThongTinCaNhan hoSoHienThi(NhanVien employee, String username) {
        String gender = giaTriTuyChon(suaMaHoaCu(employee.getGioiTinh()));
        String address = giaTriTuyChon(suaMaHoaCu(employee.getDiaChi()));
        String email = giaTriTuyChon(employee.getEmail());
        String phone = giaTriTuyChon(employee.getSoDienThoai());
        LocalDate birthday = employee.getNgaySinh();
        boolean defaultAddress = "Chưa cập nhật".equals(address);
        boolean defaultStaffEmail = email != null && GENERATED_EMAIL.matcher(email).matches();
        boolean defaultAdminEmail = "adminso1@qlykho.local".equalsIgnoreCase(email);
        // A pre-existing account can have both configured data and defaults from its original setup.
        boolean legacyAdmin = defaultAdminEmail || (
                "Quản trị viên".equals(suaMaHoaCu(employee.getChucVu()))
                        && ADMIN_DEFAULT_PHONE.equals(phone) && ADMIN_DEFAULT_BIRTHDAY.equals(birthday) && defaultAddress
        );
        if (defaultStaffEmail) {
            if (phone != null && phone.matches("0[1-9][0-9]{8}")
                    && STAFF_DEFAULT_BIRTHDAY.equals(birthday) && "Khác".equals(gender) && defaultAddress) {
                phone = null;
            }
            if (STAFF_DEFAULT_BIRTHDAY.equals(birthday)) birthday = null;
        }
        if (legacyAdmin) {
            if (ADMIN_DEFAULT_PHONE.equals(phone)) phone = null;
            if (ADMIN_DEFAULT_BIRTHDAY.equals(birthday)) birthday = null;
        }
        if ((legacyAdmin || defaultStaffEmail) && "Khác".equals(gender)) gender = null;
        return new ThongTinCaNhan(
                hoTenHienThi(employee.getHoTen(), username), gender, birthday, phone,
                defaultStaffEmail || defaultAdminEmail ? null : email, defaultAddress ? null : address
        );
    }

    static String giaTriTuyChon(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    static boolean laChucVuQuanLy(String position) {
        AccountRole role = AccountRole.fromName(suaMaHoaCu(position));
        return role == AccountRole.ADMIN || role == AccountRole.MANAGER;
    }

    static String suaMaHoaCu(String value) {
        if (value == null) {
            return "";
        }
        String text = value.strip();
        if (!StandardCharsets.ISO_8859_1.newEncoder().canEncode(text)) {
            return text;
        }
        // Some older profile defaults were read as ISO-8859-1 despite being stored as UTF-8.
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .decode(ByteBuffer.wrap(text.getBytes(StandardCharsets.ISO_8859_1))).toString();
        } catch (CharacterCodingException exception) {
            return text;
        }
    }
}
