package com.qlykho.controller;

import com.qlykho.dto.ThongKeResponse;
import com.qlykho.dto.ThongKeBaoCaoResponse;
import com.qlykho.dto.ThongKeBieuDoResponse;
import com.qlykho.service.ThongKeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/thong-ke")
public class ThongKeController {
    private final ThongKeService thongKeService;

    public ThongKeController(ThongKeService thongKeService) {
        this.thongKeService = thongKeService;
    }

    @GetMapping
    public ThongKeResponse layThongKe() {
        return thongKeService.layThongKeHienTai();
    }

    @GetMapping("/bao-cao")
    public ThongKeBaoCaoResponse layBaoCao(
            @RequestParam(required = false) LocalDate tuNgay,
            @RequestParam(required = false) LocalDate denNgay,
            @RequestParam(required = false) Integer maDM
    ) {
        return thongKeService.layBaoCao(tuNgay, denNgay, maDM);
    }

    @GetMapping("/bieu-do")
    public ThongKeBieuDoResponse layBieuDo(
            @RequestParam(defaultValue = "7-ngay") String cheDo,
            @RequestParam(required = false) LocalDate ngay,
            @RequestParam(required = false) String thang
    ) {
        return thongKeService.layBieuDo(cheDo, ngay, thang);
    }
}
