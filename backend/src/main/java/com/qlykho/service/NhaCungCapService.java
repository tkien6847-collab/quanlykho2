package com.qlykho.service;

import com.qlykho.entity.NhaCungCap;
import com.qlykho.repository.NhaCungCapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class NhaCungCapService {
    private final NhaCungCapRepository nhaCungCapRepository;

    public NhaCungCapService(NhaCungCapRepository nhaCungCapRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
    }

    public List<NhaCungCap> layDanhSachNhaCungCap() {
        return nhaCungCapRepository.findAll();
    }

    @Transactional
    public NhaCungCap themNhaCungCap(NhaCungCap nhaCungCap) {
        if (nhaCungCap == null
                || isBlank(nhaCungCap.getTenNCC())
                || isBlank(nhaCungCap.getSoDienThoai())
                || isBlank(nhaCungCap.getEmail())
                || isBlank(nhaCungCap.getDiaChi())) {
            throw new IllegalArgumentException("Thông tin nhà cung cấp không hợp lệ.");
        }
        nhaCungCap.setMaNCC(null);
        String prefix = "NCC-" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + "-";
        int nextSequence = nhaCungCapRepository.findAllByMaNccCodeStartingWith(prefix).stream()
                .map(NhaCungCap::getMaNccCode)
                .map(code -> code.substring(prefix.length()))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0) + 1;
        nhaCungCap.setMaNccCode(prefix + String.format(java.util.Locale.ROOT, "%02d", nextSequence));
        return nhaCungCapRepository.save(nhaCungCap);
    }

    public void xoaNhaCungCap(Integer maNCC) {
        if (!nhaCungCapRepository.existsById(maNCC)) {
            throw new NoSuchElementException("Không tìm thấy nhà cung cấp.");
        }
        nhaCungCapRepository.deleteById(maNCC);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}