package com.qlykho.service;

import com.qlykho.entity.HangHoa;
import com.qlykho.repository.DanhMucRepository;
import com.qlykho.repository.HangHoaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.time.LocalDate;

@Service
public class HangHoaService {
    private final HangHoaRepository hangHoaRepository;
    private final DanhMucRepository danhMucRepository;

    public HangHoaService(HangHoaRepository hangHoaRepository, DanhMucRepository danhMucRepository) {
        this.hangHoaRepository = hangHoaRepository;
        this.danhMucRepository = danhMucRepository;
    }

    @Transactional(readOnly = true)
    public List<HangHoa> layDanhSachHangHoa() {
        return hangHoaRepository.findAll().stream()
                .map(this::includeCategoryName)
                .toList();
    }

    public List<HangHoa> timHangHoaTheoDanhMuc(Integer maDM) {
        if (maDM == null || !danhMucRepository.existsById(maDM)) {
            throw new NoSuchElementException("Không tìm thấy danh mục.");
        }
        return hangHoaRepository.findByMaDM(maDM).stream()
                .map(this::includeCategoryName)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HangHoa> traCuuTonKho(String tenDanhMuc, String trangThai) {
        if (isBlank(tenDanhMuc)) {
            throw new IllegalArgumentException("Tên danh mục là bắt buộc.");
        }
        if (tenDanhMuc.trim().length() > 100) {
            throw new IllegalArgumentException("Tên danh mục không được vượt quá 100 ký tự.");
        }
        if (isBlank(trangThai) || !List.of("Còn hàng", "Hết hàng", "Sắp hết").contains(trangThai)) {
            throw new IllegalArgumentException("Trạng thái tồn kho không hợp lệ.");
        }

        return danhMucRepository.findFirstByTenDMIgnoreCase(tenDanhMuc.trim())
                .map(category -> hangHoaRepository.findByMaDM(category.getMaDM()).stream()
                        .filter(product -> matchesStockStatus(product.getSoLuongTon(), trangThai))
                        .map(product -> {
                            product.setTenDM(category.getTenDM());
                            return product;
                        })
                        .toList())
                .orElseGet(List::of);
    }

    @Transactional
    public HangHoa themHangHoa(HangHoa hangHoa) {
        resolveCategory(hangHoa);
        hangHoa.setMaHH(null);
        hangHoa.setMaSku(generateDailyCode("SP", hangHoaRepository.findAllByMaSkuStartingWith(codePrefix("SP"))));
        hangHoa.setSoLuongTon(0);
        hangHoa.setTrangThai("Hoạt động");
        validate(hangHoa);
        return hangHoaRepository.save(hangHoa);
    }

    @Transactional
    public HangHoa capNhatHangHoa(Integer maHH, HangHoa thongTinMoi) {
        HangHoa hangHoa = hangHoaRepository.findById(maHH)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy hàng hóa."));
        if (thongTinMoi != null) {
            resolveCategory(thongTinMoi);
            thongTinMoi.setMaHH(maHH);
            thongTinMoi.setMaSku(hangHoa.getMaSku());
        }
        validate(thongTinMoi);
        return hangHoaRepository.save(thongTinMoi);
    }

    @Transactional
    public void xoaHangHoa(Integer maHH) {
        if (!hangHoaRepository.existsById(maHH)) {
            throw new NoSuchElementException("Không tìm thấy hàng hóa.");
        }
        hangHoaRepository.deleteById(maHH);
    }

    private void resolveCategory(HangHoa hangHoa) {
        if (hangHoa == null || isBlank(hangHoa.getTenDM())) {
            return;
        }
        String categoryName = hangHoa.getTenDM().trim();
        if (categoryName.length() > 100) {
            throw new IllegalArgumentException("Tên danh mục không được vượt quá 100 ký tự.");
        }

        var category = danhMucRepository.findFirstByTenDMIgnoreCase(categoryName)
                .orElseGet(() -> {
                    var newCategory = new com.qlykho.entity.DanhMuc();
                    newCategory.setTenDM(categoryName);
                    return danhMucRepository.save(newCategory);
                });
        hangHoa.setMaDM(category.getMaDM());
        hangHoa.setTenDM(category.getTenDM());
    }

    private void validate(HangHoa hangHoa) {
        if (hangHoa == null
                || isBlank(hangHoa.getTenHH())
                || isBlank(hangHoa.getDonViTinh())
                || isBlank(hangHoa.getTrangThai())
                || hangHoa.getDonGia() == null
                || hangHoa.getDonGia().signum() < 0
                || hangHoa.getSoLuongTon() == null
                || hangHoa.getSoLuongTon() < 0
                || hangHoa.getMaDM() == null) {
            throw new IllegalArgumentException("Thông tin hàng hóa không hợp lệ.");
        }
        if (!danhMucRepository.existsById(hangHoa.getMaDM())) {
            throw new IllegalArgumentException("Danh mục không tồn tại.");
        }
        String location = hangHoa.getViTriKe();
        if (!isBlank(location)) {
            boolean occupied = hangHoa.getMaHH() == null
                    ? hangHoaRepository.existsByViTriKeIgnoreCase(location.trim())
                    : hangHoaRepository.existsByViTriKeIgnoreCaseAndMaHHNot(location.trim(), hangHoa.getMaHH());
            if (occupied) {
                throw new IllegalArgumentException("Vị trí kệ đã có sản phẩm.");
            }
            hangHoa.setViTriKe(location.trim());
        }
    }

    private HangHoa includeCategoryName(HangHoa hangHoa) {
        danhMucRepository.findById(hangHoa.getMaDM())
                .ifPresent(category -> hangHoa.setTenDM(category.getTenDM()));
        return hangHoa;
    }

    private boolean matchesStockStatus(Integer quantity, String status) {
        return switch (status) {
            case "Còn hàng" -> quantity > 5;
            case "Sắp hết" -> quantity > 0 && quantity <= 5;
            case "Hết hàng" -> quantity == 0;
            default -> false;
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String codePrefix(String prefix) {
        return prefix + "-" + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy")) + "-";
    }

    private String generateDailyCode(String prefix, List<HangHoa> products) {
        String codePrefix = codePrefix(prefix);
        int nextSequence = products.stream()
                .map(HangHoa::getMaSku)
                .map(code -> code.substring(codePrefix.length()))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0) + 1;
        return codePrefix + String.format(java.util.Locale.ROOT, "%02d", nextSequence);
    }
}