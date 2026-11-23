package com.qlykho.service;

import com.qlykho.dto.ThongKeResponse;
import com.qlykho.repository.ChiTietPhieuNhapRepository;
import com.qlykho.repository.ChiTietPhieuXuatRepository;
import com.qlykho.repository.PhieuNhapRepository;
import com.qlykho.repository.PhieuXuatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

@Service
public class ThongKeService {
    private static final ZoneId VIETNAM_TIME_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final String PENDING_STATUS = "Chờ duyệt";

    private final ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;
    private final ChiTietPhieuXuatRepository chiTietPhieuXuatRepository;
    private final PhieuNhapRepository phieuNhapRepository;
    private final PhieuXuatRepository phieuXuatRepository;

    public ThongKeService(
            ChiTietPhieuNhapRepository chiTietPhieuNhapRepository,
            ChiTietPhieuXuatRepository chiTietPhieuXuatRepository,
            PhieuNhapRepository phieuNhapRepository,
            PhieuXuatRepository phieuXuatRepository
    ) {
        this.chiTietPhieuNhapRepository = chiTietPhieuNhapRepository;
        this.chiTietPhieuXuatRepository = chiTietPhieuXuatRepository;
        this.phieuNhapRepository = phieuNhapRepository;
        this.phieuXuatRepository = phieuXuatRepository;
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
                zeroIfNull(chiTietPhieuXuatRepository.sumQuantityByDateRange(firstDayOfMonth, today)),
                phieuNhapRepository.countByTrangThaiIgnoreCase(PENDING_STATUS)
                        + phieuXuatRepository.countByTrangThaiIgnoreCase(PENDING_STATUS)
        );
    }

    private Long zeroIfNull(Long quantity) {
        return quantity == null ? 0L : quantity;
    }
}
