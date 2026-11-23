package com.qlykho.service;

import com.qlykho.dto.DanhMucDanhSachResponse;
import com.qlykho.dto.DanhMucLuaChon;
import com.qlykho.dto.TaoDanhMucRequest;
import com.qlykho.entity.DanhMuc;
import com.qlykho.repository.DanhMucRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DanhMucService {
    private final DanhMucRepository danhMucRepository;

    public DanhMucService(DanhMucRepository danhMucRepository) {
        this.danhMucRepository = danhMucRepository;
    }

    public List<DanhMucDanhSachResponse> layDanhSach() {
        return danhMucRepository.findDirectory();
    }

    public List<DanhMucLuaChon> layLuaChon() {
        return danhMucRepository.findAllByTrangThaiOrderByTenDMAsc(DanhMuc.HOAT_DONG).stream()
                .map(category -> new DanhMucLuaChon(category.getMaDM(), category.getTenDM()))
                .toList();
    }

    @Transactional
    public DanhMucDanhSachResponse themDanhMuc(TaoDanhMucRequest request) {
        if (request.tenDM() == null || request.tenDM().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập tên danh mục.");
        }
        String tenDM = request.tenDM().strip();
        String moTa = request.moTa() == null ? "" : request.moTa().strip();
        if (tenDM.length() > 100 || moTa.length() > 200) {
            throw new IllegalArgumentException("Tên danh mục tối đa 100 ký tự và mô tả tối đa 200 ký tự.");
        }
        if (danhMucRepository.findFirstByTenDMIgnoreCase(tenDM).isPresent()) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại.");
        }
        DanhMuc category = new DanhMuc();
        category.setTenDM(tenDM);
        category.setMoTa(moTa.isEmpty() ? null : moTa);
        try {
            DanhMuc saved = danhMucRepository.saveAndFlush(category);
            return new DanhMucDanhSachResponse(saved.getMaDM(), saved.getTenDM(), saved.getMoTa(), 0L, saved.getTrangThai());
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại.", exception);
        }
    }

    @Transactional
    public DanhMuc capNhatTrangThai(Integer maDM, String trangThai) {
        if (!DanhMuc.HOAT_DONG.equals(trangThai) && !DanhMuc.NGUNG_HOAT_DONG.equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái danh mục không hợp lệ.");
        }
        DanhMuc category = danhMucRepository.findById(maDM)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục."));
        category.setTrangThai(trangThai);
        return category;
    }
}
