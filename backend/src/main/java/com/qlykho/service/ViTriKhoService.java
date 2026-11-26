package com.qlykho.service;

import com.qlykho.entity.HangHoa;
import com.qlykho.entity.ViTriKho;
import com.qlykho.repository.HangHoaRepository;
import com.qlykho.repository.ViTriKhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Locale;

@Service
public class ViTriKhoService {
    private final ViTriKhoRepository viTriKhoRepository;
    private final HangHoaRepository hangHoaRepository;

    public ViTriKhoService(ViTriKhoRepository viTriKhoRepository, HangHoaRepository hangHoaRepository) {
        this.viTriKhoRepository = viTriKhoRepository;
        this.hangHoaRepository = hangHoaRepository;
    }

    @Transactional(readOnly = true)
    public List<ViTriKho> layDanhSachViTri() {
        return viTriKhoRepository.findAllByOrderByMaKeAscTangAscViTriOAsc();
    }

    @Transactional
    public List<ViTriKho> taoDayKe(String maKe) {
        if (maKe == null || !maKe.trim().matches("[A-Za-z0-9]{1,20}")) {
            throw new IllegalArgumentException("Mã dãy kệ chỉ gồm chữ và số, tối đa 20 ký tự.");
        }
        String rack = maKe.trim().toUpperCase(Locale.ROOT);
        List<ViTriKho> existing = viTriKhoRepository.findAllByNormalizedMaKe(rack);
        if (!existing.isEmpty()) throw new IllegalArgumentException("Dãy kệ " + rack + " đã tồn tại.");
        List<ViTriKho> slots = new ArrayList<>(20);
        for (int level = 1; level <= 4; level++) {
            for (int bin = 1; bin <= 5; bin++) {
                ViTriKho slot = new ViTriKho();
                slot.setKhuVuc("Kho chính");
                slot.setMaKe(rack);
                slot.setTang(level);
                slot.setViTriO(bin);
                slot.setSucChua(50);
                String code = String.format(Locale.ROOT, "%s-T%d-%02d", rack, level, bin);
                if (viTriKhoRepository.existsByMaViTriCodeIgnoreCase(code)) {
                    throw new IllegalArgumentException("Mã vị trí " + code + " đã được sử dụng.");
                }
                slot.setMaViTriCode(code);
                slots.add(slot);
            }
        }
        return viTriKhoRepository.saveAll(slots);
    }

    @Transactional
    public void xoaDayKe(String maKe) {
        if (maKe == null || maKe.isBlank()) throw new IllegalArgumentException("Vui lòng chọn dãy kệ cần xóa.");
        String normalizedRack = maKe.trim();
        List<ViTriKho> rackSlots = viTriKhoRepository.findAllByNormalizedMaKe(normalizedRack);
        if (rackSlots.isEmpty()) throw new NoSuchElementException("Không tìm thấy dãy kệ.");
        String rackPrefix = normalizedRack.toUpperCase(Locale.ROOT) + "-T";
        boolean hasProducts = hangHoaRepository.findAll().stream()
                .filter(product -> product.getViTriKe() != null)
                .map(product -> product.getViTriKe().trim().toUpperCase(Locale.ROOT))
                .anyMatch(location -> location.equals(normalizedRack.toUpperCase(Locale.ROOT))
                        || location.startsWith(rackPrefix)
                        || rackSlots.stream().anyMatch(slot -> slot.getMaViTriCode() != null
                                && slot.getMaViTriCode().equalsIgnoreCase(location)));
        if (hasProducts) throw new IllegalArgumentException("Không thể xóa kệ có sản phẩm.");
        viTriKhoRepository.deleteAll(rackSlots);
    }

    @Transactional
    public HangHoa chuyenViTri(Integer maHH, String maViTriCode) {
        if (maViTriCode == null || maViTriCode.isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn vị trí mới.");
        }
        HangHoa product = hangHoaRepository.findById(maHH)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy hàng hóa."));
        ViTriKho destination = viTriKhoRepository.findByMaViTriCodeIgnoreCase(maViTriCode.trim())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy vị trí kho."));
        if (product.getMaHH() == null) throw new IllegalArgumentException("Mã hàng hóa không hợp lệ.");
        boolean occupied = hangHoaRepository.findAll().stream()
                .anyMatch(item -> !item.getMaHH().equals(maHH)
                        && item.getViTriKe() != null
                        && item.getViTriKe().equalsIgnoreCase(destination.getMaViTriCode()));
        if (occupied) throw new IllegalArgumentException("Vị trí này đã có sản phẩm.");
        product.setViTriKe(destination.getMaViTriCode());
        return hangHoaRepository.save(product);
    }
}
