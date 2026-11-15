package com.qlykho.controller;

import com.qlykho.entity.HangHoa;
import com.qlykho.service.HangHoaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hang-hoa")
public class HangHoaController {
    private final HangHoaService hangHoaService;

    public HangHoaController(HangHoaService hangHoaService) {
        this.hangHoaService = hangHoaService;
    }

    @GetMapping
    public List<HangHoa> layDanhSachHangHoa() {
        return hangHoaService.layDanhSachHangHoa();
    }

    @GetMapping("/tra-cuu")
    public List<HangHoa> traCuuTonKho(
            @RequestParam String tenDanhMuc,
            @RequestParam String trangThai
    ) {
        return hangHoaService.traCuuTonKho(tenDanhMuc, trangThai);
    }

    @GetMapping("/danh-muc/{maDM}")
    public List<HangHoa> timHangHoaTheoDanhMuc(@PathVariable Integer maDM) {
        return hangHoaService.timHangHoaTheoDanhMuc(maDM);
    }

    @PostMapping
    public ResponseEntity<HangHoa> themHangHoa(@RequestBody HangHoa hangHoa) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hangHoaService.themHangHoa(hangHoa));
    }

    @PutMapping("/{maHH}")
    public HangHoa capNhatHangHoa(@PathVariable Integer maHH, @RequestBody HangHoa hangHoa) {
        return hangHoaService.capNhatHangHoa(maHH, hangHoa);
    }

    @DeleteMapping("/{maHH}")
    public ResponseEntity<Void> xoaHangHoa(@PathVariable Integer maHH) {
        hangHoaService.xoaHangHoa(maHH);
        return ResponseEntity.noContent().build();
    }
}
