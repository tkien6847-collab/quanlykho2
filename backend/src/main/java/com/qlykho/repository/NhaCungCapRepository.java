package com.qlykho.repository;

import com.qlykho.entity.NhaCungCap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, Integer> {
    java.util.List<NhaCungCap> findAllByMaNccCodeStartingWith(String prefix);
}
