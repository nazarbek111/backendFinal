package kz.narxoz.backend.service;

import kz.narxoz.backend.dto.request.LoginRequest;
import kz.narxoz.backend.dto.request.RefreshTokenRequest;
import kz.narxoz.backend.dto.request.RegisterRequest;
import kz.narxoz.backend.dto.response.AuthResponse;
import kz.narxoz.backend.entity.Parent;
import kz.narxoz.backend.entity.RefreshToken;
import kz.narxoz.backend.entity.enums.Role;
import kz.narxoz.backend.repository.ParentRepository;
import kz.narxoz.backend.repository.RefreshTokenRepository;
import kz.narxoz.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ParentRepository parentRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Проверку на существование оставляем
        if (parentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Parent parent = Parent.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.PARENT)
                .build();

        parentRepository.save(parent);
        return buildAuthResponse(parent);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Parent parent = parentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!parent.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return buildAuthResponse(parent);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (stored.isExpired()) {
            refreshTokenRepository.delete(stored);
            throw new RuntimeException("Refresh token expired. Please log in again.");
        }

        Parent parent = stored.getParent();

        refreshTokenRepository.delete(stored);
        return buildAuthResponse(parent);
    }

    @Transactional
    public void logout(String email) {
        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
        refreshTokenRepository.deleteAllByParentId(parent.getId());
    }

    // ===== Helper =====
    private AuthResponse buildAuthResponse(Parent parent) {
        String accessToken = jwtUtil.generateToken(parent.getEmail(), parent.getRole().name());
        String rawRefresh = jwtUtil.generateRefreshToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .parent(parent)
                .token(rawRefresh)
                .expiresAt(jwtUtil.getRefreshTokenExpiry().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                .build();

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, rawRefresh, parent.getEmail(), parent.getRole().name());
    }
}
