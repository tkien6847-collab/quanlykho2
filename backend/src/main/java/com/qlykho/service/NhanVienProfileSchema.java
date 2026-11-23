package com.qlykho.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NhanVienProfileSchema implements ApplicationRunner {
    private final JdbcTemplate jdbc;

    public NhanVienProfileSchema(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Hibernate's update mode does not relax existing NOT NULL constraints.
        List<String> changes = jdbc.query("""
                SELECT COLUMN_NAME, COLUMN_TYPE, CHARACTER_SET_NAME, COLLATION_NAME
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'nhanvien'
                  AND IS_NULLABLE = 'NO'
                  AND COLUMN_NAME IN ('GioiTinh', 'NgaySinh', 'SoDienThoai', 'Email', 'DiaChi')
                """, (row, index) -> {
            String charset = row.getString("CHARACTER_SET_NAME");
            String collation = charset == null ? "" : " CHARACTER SET " + charset + " COLLATE "
                    + row.getString("COLLATION_NAME");
            return "MODIFY COLUMN " + row.getString("COLUMN_NAME") + " " + row.getString("COLUMN_TYPE") + collation + " NULL";
        });
        if (!changes.isEmpty()) {
            jdbc.execute("ALTER TABLE NHANVIEN " + String.join(", ", changes));
        }
    }
}
