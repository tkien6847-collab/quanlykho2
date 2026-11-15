package com.qlykho.repository;

import com.qlykho.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
    java.util.Optional<DanhMuc> findFirstByTenDMIgnoreCase(String tenDM);
}
