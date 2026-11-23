package com.qlykho.service;

import com.qlykho.dto.KhachHangLuaChon;
import com.qlykho.entity.KhachHang;
import com.qlykho.repository.KhachHangRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class KhachHangService {
    private final KhachHangRepository khachHangRepository;

    public KhachHangService(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }
    
    //ví dụ về hàm lấy danh sách 
    public List<KhachHang> layDanhSachKhachhang(){
        return khachHangRepository.findAll();
    }

    public List<KhachHangLuaChon> layLuaChonKhachHang() {
        return khachHangRepository.findAllByTrangThaiOrderByHoTenAscMaKHAsc(KhachHang.HOAT_DONG).stream()
                .map(customer -> new KhachHangLuaChon(customer.getMaKH(), customer.getHoTen()))
                .toList();
    }
    
    //ví dụ hàm thêm thông tin 
    public KhachHang themKhachHang(KhachHang khachHang){
        if (khachHang == null) {
            throw new IllegalArgumentException("Thông tin khách hàng không được để trống.");
        }
        KhachHang kh = new KhachHang();
        kh.setHoTen(khachHang.getHoTen());
        kh.setSoDienThoai(khachHang.getSoDienThoai());
        kh.setEmail(khachHang.getEmail());
        kh.setDiaChi(khachHang.getDiaChi());

        try {
            return khachHangRepository.save(kh);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
"Không thể thêm khách hàng: email hoặc số điện thoại đã tồn tại, hoặc dữ liệu không hợp lệ.", e);
        }
      }

    @Transactional
    public KhachHang capNhatTrangThai(Integer maKH, String trangThai) {
        if (!KhachHang.HOAT_DONG.equals(trangThai) && !KhachHang.NGUNG_HOAT_DONG.equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái khách hàng không hợp lệ.");
        }
        KhachHang customer = khachHangRepository.findById(maKH)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khách hàng."));
        customer.setTrangThai(trangThai);
        return customer;
    }
    
   // ví dụ hàm xóa
    public void xoaKhachHang(Integer maKH) {
        //cái exist là ktra có trong DB k trả về true false
        if (maKH == null || !khachHangRepository.existsById(maKH)) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng có mã: " + maKH);
        }

        try {
            khachHangRepository.deleteById(maKH);
        } catch (DataAccessException e) {
            throw new IllegalStateException
            ("Không thể xóa khách hàng có mã: " + maKH, e);
        }
    }
}
