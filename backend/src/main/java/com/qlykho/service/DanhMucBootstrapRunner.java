package com.qlykho.service;

import com.qlykho.entity.DanhMuc;
import com.qlykho.repository.DanhMucRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DanhMucBootstrapRunner implements ApplicationRunner {
    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "Đồ gia dụng",
            "Điện tử",
            "Thực phẩm",
            "Đồ uống",
            "Văn phòng phẩm",
            "Quần áo",
            "Mỹ phẩm",
            "Thiết bị",
            "Phụ kiện",
            "Khác"
    );

    private final DanhMucRepository danhMucRepository;

    public DanhMucBootstrapRunner(DanhMucRepository danhMucRepository) {
        this.danhMucRepository = danhMucRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (String categoryName : DEFAULT_CATEGORIES) {
            if (danhMucRepository.findFirstByTenDMIgnoreCase(categoryName).isEmpty()) {
                DanhMuc category = new DanhMuc();
                category.setTenDM(categoryName);
                danhMucRepository.save(category);
            }
        }
    }
}
