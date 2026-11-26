package com.qlykho.repository;

import com.qlykho.entity.ViTriKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ViTriKhoRepository extends JpaRepository<ViTriKho, Integer> {
    List<ViTriKho> findAllByOrderByMaKeAscTangAscViTriOAsc();
    List<ViTriKho> findAllByMaKeIgnoreCaseOrderByTangAscViTriOAsc(String maKe);
    List<ViTriKho> findAllByMaKeIgnoreCase(String maKe);
    @Query("select v from ViTriKho v where upper(trim(v.maKe)) = upper(trim(:maKe))")
    List<ViTriKho> findAllByNormalizedMaKe(@Param("maKe") String maKe);
    Optional<ViTriKho> findByMaViTriCodeIgnoreCase(String maViTriCode);
    boolean existsByMaViTriCodeIgnoreCase(String maViTriCode);
}
