-- Du lieu khoi tao cho he thong Quan ly kho (MySQL 8+).
-- Chay sau khi schema da duoc tao boi Spring Boot/Hibernate (ddl-auto=update).
-- Mat khau mac dinh admin la Admin@12345; chuoi duoi day la BCrypt strength 10.

USE qlykho;

SET NAMES utf8mb4;

-- Them cac vai tro he thong neu chua co.
INSERT INTO VAITRO (TenVaiTro, MoTa)
SELECT _utf8mb4 0x5175E1BAA36E207472E1BB8B207669C3AA6E, _utf8mb4 0x5175E1BAA36E207472E1BB8B2068E1BB8779207468E1BB916E67
WHERE NOT EXISTS (SELECT 1 FROM VAITRO WHERE TenVaiTro = 'Quản trị viên');

INSERT INTO VAITRO (TenVaiTro, MoTa)
SELECT _utf8mb4 0x5175E1BAA36E206CC3BD, _utf8mb4 0x5175E1BAA36E206CC3BD206B686F
WHERE NOT EXISTS (SELECT 1 FROM VAITRO WHERE TenVaiTro = 'Quản lý');

INSERT INTO VAITRO (TenVaiTro, MoTa)
SELECT _utf8mb4 0x4E68C3A26E207669C3AA6E, _utf8mb4 0x4E68C3A26E207669C3AA6E206B686F
WHERE NOT EXISTS (SELECT 1 FROM VAITRO WHERE TenVaiTro = 'Nhân viên');

-- Tao ho so nhan vien quan tri neu tai khoan admin chua ton tai.
INSERT INTO NHANVIEN (HoTen, ChucVu)
SELECT _utf8mb4 0x5175E1BAA36E207472E1BB8B207669C3AA6E, _utf8mb4 0x5175E1BAA36E207472E1BB8B207669C3AA6E
WHERE NOT EXISTS (SELECT 1 FROM TAIKHOAN WHERE TenDangNhap = 'adminso2')
  AND NOT EXISTS (
      SELECT 1 FROM NHANVIEN
      WHERE HoTen = 'Quản trị viên' AND ChucVu = 'Quản trị viên'
        AND MaNV NOT IN (SELECT MaNV FROM TAIKHOAN)
  );

-- Tao tai khoan admin. MaNV duoc chon la ho so quan tri chua gan tai khoan.
-- ON DUPLICATE KEY UPDATE la no-op de chay lai script khong doi mat khau hien co.
INSERT INTO TAIKHOAN (TenDangNhap, MatKhau, TrangThai, MaVaiTro, MaNV)
SELECT 'adminso2',
       '$2a$10$tDZGsMblgdF3oKFs6yrGfuo08wE.1B23A5kq9Bq.CzPWxCquRSp3.',
       _utf8mb4 0x486FE1BAA17420C491E1BB996E67,
       (SELECT MaVaiTro FROM VAITRO WHERE TenVaiTro = 'Quản trị viên' LIMIT 1),
       (SELECT nv.MaNV
        FROM NHANVIEN nv
        WHERE nv.ChucVu = 'Quản trị viên'
          AND NOT EXISTS (SELECT 1 FROM TAIKHOAN tk WHERE tk.MaNV = nv.MaNV)
        ORDER BY nv.MaNV
        LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM TAIKHOAN WHERE TenDangNhap = 'adminso2')
ON DUPLICATE KEY UPDATE TenDangNhap = VALUES(TenDangNhap);
