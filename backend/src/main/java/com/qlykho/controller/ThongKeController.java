package com.qlykho.controller;

import com.qlykho.dto.ThongKeResponse;
import com.qlykho.service.ThongKeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
