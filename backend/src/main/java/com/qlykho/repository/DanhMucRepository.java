package com.qlykho.repository;

import com.qlykho.dto.DanhMucDanhSachResponse;
import com.qlykho.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
    Optional<DanhMuc> findFirstByTenDMIgnoreCase(String tenDM);

    List<DanhMuc> findAllByTrangThaiOrderByTenDMAsc(String trangThai);

    @Query("""
            select new com.qlykho.dto.DanhMucDanhSachResponse(dm.maDM, dm.tenDM, dm.moTa, count(hh.maHH), dm.trangThai)
            from DanhMuc dm left join HangHoa hh on hh.maDM = dm.maDM
            group by dm.maDM, dm.tenDM, dm.moTa, dm.trangThai
            order by dm.maDM desc
            """)
    List<DanhMucDanhSachResponse> findDirectory();
}
