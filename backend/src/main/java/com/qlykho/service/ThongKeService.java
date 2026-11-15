package com.qlykho.service;

import com.qlykho.dto.ThongKeResponse;
import com.qlykho.repository.ChiTietPhieuNhapRepository;
import com.qlykho.repository.ChiTietPhieuXuatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

@Service
public class ThongKeService {
    private static final ZoneId VIETNAM_TIME_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;
    private final ChiTietPhieuXuatRepository chiTietPhieuXuatRepository;

    public ThongKeService(
            ChiTietPhieuNhapRepository chiTietPhieuNhapRepository,
            ChiTietPhieuXuatRepository chiTietPhieuXuatRepository
    ) {
        this.chiTietPhieuNhapRepository = chiTietPhieuNhapRepository;
        this.chiTietPhieuXuatRepository = chiTietPhieuXuatRepository;
    }

    public ThongKeResponse layThongKeHienTai() {
        LocalDate today = LocalDate.now(VIETNAM_TIME_ZONE);
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate firstDayOfMonth = currentMonth.atDay(1);

        return new ThongKeResponse(
                today,
                currentMonth.toString(),
                zeroIfNull(chiTietPhieuNhapRepository.sumQuantityByDate(today)),
                zeroIfNull(chiTietPhieuXuatRepository.sumQuantityByDate(today)),
                zeroIfNull(chiTietPhieuNhapRepository.sumQuantityByDateRange(firstDayOfMonth, today)),
                zeroIfNull(chiTietPhieuXuatRepository.sumQuantityByDateRange(firstDayOfMonth, today))
        );
    }

    private Long zeroIfNull(Long quantity) {
        return quantity == null ? 0L : quantity;
    }
}
