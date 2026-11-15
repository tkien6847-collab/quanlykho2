package com.qlykho.repository;

import com.qlykho.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaiTroRepository extends JpaRepository<VaiTro, Integer> {
    java.util.Optional<VaiTro> findByTenVaiTro(String tenVaiTro);
}
