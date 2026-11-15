package com.qlykho.security;

import com.qlykho.repository.TaiKhoanRepository;
import com.qlykho.repository.VaiTroRepository;
import com.qlykho.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final TaiKhoanRepository taiKhoanRepository;
    private final VaiTroRepository vaiTroRepository;
    @Value("${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
private String secretKey;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            TaiKhoanRepository taiKhoanRepository,
            VaiTroRepository vaiTroRepository
    ) {
        this.jwtService = jwtService;
        this.taiKhoanRepository = taiKhoanRepository;
        this.vaiTroRepository = vaiTroRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            if (jwtService.isValid(token)) {
                String username = jwtService.extractUsername(token);
                taiKhoanRepository.findByTenDangNhap(username)
                        .filter(account -> isActive(account.getTrangThai()))
                        .flatMap(account -> vaiTroRepository.findById(account.getMaVaiTro())
                                .map(role -> new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        java.util.List.of(new SimpleGrantedAuthority(authority(role.getTenVaiTro())))
                                )))
                        .ifPresent(SecurityContextHolder.getContext()::setAuthentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isActive(String status) {
        if (status == null) return false;
        String normalized = status.trim().toLowerCase(Locale.ROOT);
        return normalized.equals("hoạt động")
                || normalized.equals("dang hoat dong")
                || normalized.equals("đang hoạt động")
                || normalized.equals("active")
                || normalized.equals("enabled");
    }

    private String authority(String roleName) {
        String normalized = Normalizer.normalize(roleName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        if (normalized.contains("admin") || normalized.contains("quan tri")) return "ROLE_ADMIN";
        if (normalized.contains("manager") || normalized.contains("quan ly")) return "ROLE_MANAGER";
        if (normalized.contains("employee") || normalized.contains("nhan vien")) return "ROLE_EMPLOYEE";
        return "ROLE_UNKNOWN";
    }
}
