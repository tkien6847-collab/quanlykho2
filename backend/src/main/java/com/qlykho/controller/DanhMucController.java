package com.qlykho.controller;

import com.qlykho.dto.CapNhatTrangThaiRequest;
import com.qlykho.dto.DanhMucDanhSachResponse;
import com.qlykho.dto.DanhMucLuaChon;
import com.qlykho.dto.TaoDanhMucRequest;
import com.qlykho.entity.DanhMuc;
import com.qlykho.service.DanhMucService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/danh-muc")
public class DanhMucController {
    private final DanhMucService danhMucService;

    public DanhMucController(DanhMucService danhMucService) {
        this.danhMucService = danhMucService;
    }

    @GetMapping
    public List<DanhMucDanhSachResponse> layDanhSachDanhMuc() {
        return danhMucService.layDanhSach();
    }

    @GetMapping("/lua-chon")
    public List<DanhMucLuaChon> layLuaChonDanhMuc() {
        return danhMucService.layLuaChon();
    }

    @PostMapping
    public ResponseEntity<DanhMucDanhSachResponse> themDanhMuc(@RequestBody TaoDanhMucRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(danhMucService.themDanhMuc(request));
    }

    @PatchMapping("/{maDM}/trang-thai")
    public DanhMuc capNhatTrangThai(
            @PathVariable("maDM") Integer maDM,
            @RequestBody CapNhatTrangThaiRequest request
    ) {
        return danhMucService.capNhatTrangThai(maDM, request.trangThai());
    }
}
