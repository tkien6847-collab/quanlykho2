package com.qlykho.controller;

import com.qlykho.entity.NhaCungCap;
import com.qlykho.service.NhaCungCapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/nha-cung-cap")
public class NhaCungCapController {
    private final NhaCungCapService nhaCungCapService;

    public NhaCungCapController(NhaCungCapService nhaCungCapService) {
        this.nhaCungCapService = nhaCungCapService;
    }

    @GetMapping
    public List<NhaCungCap> layDanhSachNhaCungCap() {
        return nhaCungCapService.layDanhSachNhaCungCap();
    }

    @PostMapping
    public ResponseEntity<NhaCungCap> themNhaCungCap(@RequestBody NhaCungCap nhaCungCap) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nhaCungCapService.themNhaCungCap(nhaCungCap));
    }

    @DeleteMapping("/{maNCC}")
    public ResponseEntity<Void> xoaNhaCungCap(@PathVariable Integer maNCC) {
        nhaCungCapService.xoaNhaCungCap(maNCC);
        return ResponseEntity.noContent().build();
    }
}
