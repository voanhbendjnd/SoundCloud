package djnd.project.SoundCloud.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.util.Base64;

import djnd.project.SoundCloud.domain.ResLoginDTO;
import djnd.project.SoundCloud.domain.entity.Permission;
import djnd.project.SoundCloud.repositories.RoleRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityUtils {
    final JwtEncoder jwtEncoder;
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    final RoleRepository roleRepository;

    public SecurityUtils(JwtEncoder jwtEncoder, RoleRepository roleRepository) {
        this.jwtEncoder = jwtEncoder;
        this.roleRepository = roleRepository;
    }

    @Value("${djnd.jwt.base64-secret}")
    String jwtKey;
    @Value("${djnd.jwt.access-token-validity-in-seconds}")
    Long accessTokenExpiration;
    @Value("${djnd.jwt.access-token-validity-in-seconds}")
    Long refreshTokenExpiration;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    public Jwt checkValidRefreshToken(String token) {
        var jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(SecurityUtils.JWT_ALGORITHM)
                .build();
        try {
            return jwtDecoder.decode(token);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public String createAccessToken(String email, ResLoginDTO dto, String sessionId) {
        var userToken = new ResLoginDTO.UserInsideToken();
        userToken.setId(dto.getUser().getId());
        userToken.setEmail(dto.getUser().getEmail());
        userToken.setName(dto.getUser().getName());
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);
        var role = this.roleRepository.findByName(dto.getUser().getRole());
        List<String> listAuthority = new ArrayList<>();
        if (role != null) {
            listAuthority = role.getPermissions()
                    .stream()
                    .map(Permission::getName)
                    .collect(Collectors.toList());
        }
        JwtClaimsSet claim = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userToken)
                .claim("permission", listAuthority)
                .claim("sessionId", sessionId)
                .build();
        var jwtHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader, claim)).getTokenValue();

    }

    public static Optional<String> getCurrentUserLogin() {
        var securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    public static Optional<String> getCurrentUserJWT() {
        var securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(a -> a.getCredentials() instanceof String)
                .map(a -> (String) a.getCredentials());
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject(); // user name
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    public String createRefreshToken(String email, ResLoginDTO dto) {
        var userToken = new ResLoginDTO.UserInsideToken();
        userToken.setEmail(dto.getUser().getEmail());
        userToken.setId(dto.getUser().getId());
        userToken.setName(dto.getUser().getName());
        var now = Instant.now();
        var validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);
        var claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userToken)
                .build();
        var jwtHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader, claims)).getTokenValue();
    }

    public static Optional<String> getCurrentSessionId() {
        var securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() != null
                && securityContext.getAuthentication().getPrincipal() instanceof Jwt jwt) {
            return Optional.ofNullable(jwt.getClaimAsString("sessionId"));
        }
        return Optional.empty();
    }
}
