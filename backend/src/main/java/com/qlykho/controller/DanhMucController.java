package com.qlykho.controller;

import com.qlykho.entity.DanhMuc;
import com.qlykho.repository.DanhMucRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/danh-muc")
public class DanhMucController {
    private final DanhMucRepository danhMucRepository;

    public DanhMucController(DanhMucRepository danhMucRepository) {
        this.danhMucRepository = danhMucRepository;
    }

    @GetMapping
    public List<DanhMuc> layDanhSachDanhMuc() {
        return danhMucRepository.findAll();
    }
}
