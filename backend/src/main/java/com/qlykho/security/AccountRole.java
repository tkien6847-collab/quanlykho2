package com.qlykho.security;

import java.text.Normalizer;
import java.util.Locale;

public enum AccountRole {
    ADMIN, MANAGER, EMPLOYEE, UNKNOWN;

    public static AccountRole fromName(String roleName) {
        if (roleName == null) return UNKNOWN;
        String normalized = Normalizer.normalize(roleName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        if (normalized.contains("admin") || normalized.contains("quan tri")) return ADMIN;
        if (normalized.contains("manager") || normalized.contains("quan ly")) return MANAGER;
        if (normalized.contains("employee") || normalized.contains("nhan vien")) return EMPLOYEE;
        return UNKNOWN;
    }
}
