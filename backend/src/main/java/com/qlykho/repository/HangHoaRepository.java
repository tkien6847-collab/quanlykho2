package com.qlykho.repository;

import com.qlykho.entity.HangHoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HangHoaRepository extends JpaRepository<HangHoa, Integer> {
    List<HangHoa> findByMaDM(Integer maDM);
    List<HangHoa> findAllByMaSkuStartingWith(String prefix);
    boolean existsByViTriKeIgnoreCase(String viTriKe);
    boolean existsByViTriKeIgnoreCaseAndMaHHNot(String viTriKe, Integer maHH);
}
