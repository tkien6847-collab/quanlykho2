package com.qlykho.service;

import com.qlykho.dto.ThongKeResponse;
import com.qlykho.dto.ThongKeBaoCaoResponse;
import com.qlykho.dto.ThongKeBieuDoResponse;
import com.qlykho.entity.HangHoa;
import com.qlykho.repository.ChiTietPhieuNhapRepository;
import com.qlykho.repository.ChiTietPhieuXuatRepository;
import com.qlykho.repository.HangHoaRepository;
import com.qlykho.repository.DanhMucRepository;
import com.qlykho.repository.PhieuNhapRepository;
import com.qlykho.repository.PhieuXuatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ThongKeService {
    private static final ZoneId VIETNAM_TIME_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final String PENDING_STATUS = "Chờ duyệt";

    private final ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;
    private final ChiTietPhieuXuatRepository chiTietPhieuXuatRepository;
    private final PhieuNhapRepository phieuNhapRepository;
    private final PhieuXuatRepository phieuXuatRepository;
    private final HangHoaRepository hangHoaRepository;
    private final DanhMucRepository danhMucRepository;

    public ThongKeService(
            ChiTietPhieuNhapRepository chiTietPhieuNhapRepository,
            ChiTietPhieuXuatRepository chiTietPhieuXuatRepository,
            PhieuNhapRepository phieuNhapRepository,
            PhieuXuatRepository phieuXuatRepository,
            HangHoaRepository hangHoaRepository,
            DanhMucRepository danhMucRepository
    ) {
        this.chiTietPhieuNhapRepository = chiTietPhieuNhapRepository;
        this.chiTietPhieuXuatRepository = chiTietPhieuXuatRepository;
        this.phieuNhapRepository = phieuNhapRepository;
        this.phieuXuatRepository = phieuXuatRepository;
        this.hangHoaRepository = hangHoaRepository;
        this.danhMucRepository = danhMucRepository;
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

    public ThongKeBaoCaoResponse layBaoCao(LocalDate tuNgay, LocalDate denNgay, Integer maDM) {
        LocalDate end = denNgay == null ? LocalDate.now(VIETNAM_TIME_ZONE) : denNgay;
        LocalDate start = tuNgay == null ? end : tuNgay;
        if (start.isAfter(end)) throw new IllegalArgumentException("Từ ngày không được sau đến ngày.");

        Map<Integer, Long> inboundByProduct = sumByProduct(chiTietPhieuNhapRepository.sumQuantityByProductAndDateRange(start, end));
        Map<Integer, Long> outboundByProduct = sumByProduct(chiTietPhieuXuatRepository.sumQuantityByProductAndDateRange(start, end));
        Map<Integer, HangHoa> products = new HashMap<>();
        hangHoaRepository.findAll().forEach(product -> products.put(product.getMaHH(), product));

        Map<Integer, long[]> categoryTotals = new LinkedHashMap<>();
        long inbound = 0;
        long outbound = 0;
        for (Integer productId : union(inboundByProduct, outboundByProduct)) {
            HangHoa product = products.get(productId);
            if (product == null || (maDM != null && !maDM.equals(product.getMaDM()))) continue;
            long in = inboundByProduct.getOrDefault(productId, 0L);
            long out = outboundByProduct.getOrDefault(productId, 0L);
            inbound += in;
            outbound += out;
            long[] totals = categoryTotals.computeIfAbsent(product.getMaDM(), ignored -> new long[2]);
            totals[0] += in;
            totals[1] += out;
        }
        Map<Integer, String> categoryNames = new HashMap<>();
        danhMucRepository.findAll().forEach(category -> categoryNames.put(category.getMaDM(), category.getTenDM()));
        List<ThongKeBaoCaoResponse.ThongKeTheoDanhMuc> categories = categoryTotals.entrySet().stream()
                .map(entry -> new ThongKeBaoCaoResponse.ThongKeTheoDanhMuc(entry.getKey(),
                        categoryNames.getOrDefault(entry.getKey(), "Danh mục #" + entry.getKey()), entry.getValue()[0], entry.getValue()[1]))
                .toList();
        return new ThongKeBaoCaoResponse(start, end, inbound, outbound, categories);
    }

    public ThongKeBieuDoResponse layBieuDo(String cheDo, LocalDate ngayChon, String thangChon) {
        LocalDate today = LocalDate.now(VIETNAM_TIME_ZONE);
        String mode = cheDo == null ? "7-ngay" : cheDo;
        LocalDate end = today;
        LocalDate start;
        switch (mode) {
            case "ngay" -> { start = ngayChon == null ? today : ngayChon; end = start; }
            case "30-ngay" -> start = end.minusDays(29);
            case "12-thang" -> { end = end.withDayOfMonth(1).plusMonths(1).minusDays(1); start = end.withDayOfMonth(1).minusMonths(11); }
            case "thang" -> {
                YearMonth selected = thangChon == null || thangChon.isBlank() ? YearMonth.from(today) : YearMonth.parse(thangChon);
                start = selected.atDay(1); end = selected.atEndOfMonth();
            }
            default -> { mode = "7-ngay"; start = end.minusDays(6); }
        }
        LocalDate queryStart = start;
        Map<LocalDate, Long> inbound = sumByDate(chiTietPhieuNhapRepository.sumQuantityByEachDate(queryStart, end));
        Map<LocalDate, Long> outbound = sumByDate(chiTietPhieuXuatRepository.sumQuantityByEachDate(queryStart, end));
        List<ThongKeBieuDoResponse.MocThongKe> points = new ArrayList<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yy");
        long stock = zeroIfNull(hangHoaRepository.findAll().stream().map(HangHoa::getSoLuongTon).filter(java.util.Objects::nonNull).mapToLong(Integer::longValue).sum());
        stock -= zeroIfNull(chiTietPhieuNhapRepository.sumQuantityBeforeDate(start));
        stock += zeroIfNull(chiTietPhieuXuatRepository.sumQuantityBeforeDate(start));
        for (LocalDate date = start; !date.isAfter(end); date = mode.equals("12-thang") ? date.plusMonths(1).withDayOfMonth(1) : date.plusDays(1)) {
            LocalDate pointDate = mode.equals("12-thang") ? date.withDayOfMonth(1) : date;
            LocalDate periodEnd = mode.equals("12-thang") ? pointDate.withDayOfMonth(1).plusMonths(1).minusDays(1) : pointDate;
            long in = inbound.entrySet().stream().filter(entry -> !entry.getKey().isBefore(pointDate) && !entry.getKey().isAfter(periodEnd)).mapToLong(Map.Entry::getValue).sum();
            long out = outbound.entrySet().stream().filter(entry -> !entry.getKey().isBefore(pointDate) && !entry.getKey().isAfter(periodEnd)).mapToLong(Map.Entry::getValue).sum();
            stock += in - out;
            String label = mode.equals("12-thang") ? pointDate.format(monthFormatter) : mode.equals("ngay") || mode.equals("thang") ? pointDate.format(DateTimeFormatter.ofPattern("dd/MM")) : pointDate.format(DateTimeFormatter.ofPattern("EEE", java.util.Locale.forLanguageTag("vi-VN")));
            points.add(new ThongKeBieuDoResponse.MocThongKe(pointDate, label, in, out, stock));
        }
        return new ThongKeBieuDoResponse(mode, points);
    }

    private Map<Integer, Long> sumByProduct(List<Object[]> rows) {
        Map<Integer, Long> result = new HashMap<>();
        rows.forEach(row -> result.put((Integer) row[0], ((Number) row[1]).longValue()));
        return result;
    }

    private Map<LocalDate, Long> sumByDate(List<Object[]> rows) {
        Map<LocalDate, Long> result = new HashMap<>();
        rows.forEach(row -> result.put((LocalDate) row[0], ((Number) row[1]).longValue()));
        return result;
    }

    private java.util.Set<Integer> union(Map<Integer, Long> first, Map<Integer, Long> second) {
        java.util.Set<Integer> result = new java.util.HashSet<>(first.keySet());
        result.addAll(second.keySet());
        return result;
    }

    private Long zeroIfNull(Long quantity) {
        return quantity == null ? 0L : quantity;
    }
}
