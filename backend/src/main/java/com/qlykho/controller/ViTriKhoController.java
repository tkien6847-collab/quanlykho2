package com.qlykho.controller;

import com.qlykho.dto.ChuyenViTriRequest;
import com.qlykho.entity.HangHoa;
import com.qlykho.entity.ViTriKho;
import com.qlykho.service.ViTriKhoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vi-tri-kho")
public class ViTriKhoController {
    private final ViTriKhoService viTriKhoService;

    public ViTriKhoController(ViTriKhoService viTriKhoService) { this.viTriKhoService = viTriKhoService; }

    @GetMapping
    public List<ViTriKho> layDanhSachViTri() { return viTriKhoService.layDanhSachViTri(); }

    @PostMapping("/day-ke")
    public List<ViTriKho> taoDayKe(@RequestBody Map<String, String> request) {
        return viTriKhoService.taoDayKe(request.get("maKe"));
    }

    @DeleteMapping("/day-ke/{maKe}")
    public void xoaDayKe(@PathVariable String maKe) {
        viTriKhoService.xoaDayKe(maKe);
    }

    @PatchMapping("/hang-hoa/{maHH}")
    public HangHoa chuyenViTri(@PathVariable Integer maHH, @RequestBody ChuyenViTriRequest request) {
        return viTriKhoService.chuyenViTri(maHH, request.maViTriCode());
    }
}
