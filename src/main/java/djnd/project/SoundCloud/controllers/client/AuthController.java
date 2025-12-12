package djnd.project.SoundCloud.controllers.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import djnd.project.SoundCloud.configs.CustomUserDetails;
import djnd.project.SoundCloud.domain.ResLoginDTO;
import djnd.project.SoundCloud.domain.request.LoginDTO;
import djnd.project.SoundCloud.domain.request.users.UserDTO;
import djnd.project.SoundCloud.services.SessionManager;
import djnd.project.SoundCloud.services.UserService;
import djnd.project.SoundCloud.utils.SecurityUtils;
import djnd.project.SoundCloud.utils.annotation.ApiMessage;
import djnd.project.SoundCloud.utils.error.PasswordMismatchException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthController {
    UserService userService;
    AuthenticationManagerBuilder builder;
    SessionManager sessionManager;
    SecurityUtils securityUtils;
    @Value("${djnd.jwt.refresh-token-validity-in-seconds}")
    Long refreshTokenExpiration;

    public AuthController(UserService userService,
            AuthenticationManagerBuilder builder, SessionManager sessionManager, SecurityUtils securityUtils) {
        this.userService = userService;
        this.sessionManager = sessionManager;
        this.securityUtils = securityUtils;
        this.builder = builder;
    }

    @PostMapping("/login")
    @ApiMessage("Login account")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO dto) throws BadCredentialsException {
        var at = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        var auth = this.builder.getObject().authenticate(at);
        SecurityContextHolder.getContext().setAuthentication(auth);
        var res = new ResLoginDTO();
        var principal = auth.getPrincipal();
        var customUser = (CustomUserDetails) principal;
        var user = customUser.getUser();
        var userLogin = new ResLoginDTO.UserLogin();
        userLogin.setEmail(user.getEmail());
        userLogin.setId(user.getId());
        userLogin.setName(user.getName());
        userLogin.setRole(user.getRole().getName());
        res.setUser(userLogin);

        var sessionID = this.sessionManager.createNewSession(user);
        String accessToken = this.securityUtils.createAccessToken(user.getEmail(), res, sessionID);
        res.setAccessToken(accessToken);
        String refreshToken = this.securityUtils.createRefreshToken(user.getEmail(), res);

        this.userService.updateRefreshTokenByEmail(user.getEmail(), refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(res);
    }

    @PostMapping("/register")
    @ApiMessage("Sign in account with email")
    public ResponseEntity<Long> register(@RequestBody @Valid UserDTO dto) {
        if (!dto.getConfirmPassword().equals(dto.getPassword())) {
            throw new PasswordMismatchException("Password and Confirm Password not the same!");
        }
        return ResponseEntity.ok(this.userService.register(dto));

    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Create new Resfresh and Access Token when User back")
    public ResponseEntity<ResLoginDTO> resetRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "invalid") String refreshToken) {
        if (refreshToken.equals("invalid")) {
            throw new BadCredentialsException("Refresh Token Invalid!");
        }
        var res = this.userService.handleRefreshTokenWithCondition(refreshToken, "refresh");
        var cookie = ResponseCookie.from("refresh_token", res.getRefreshToken())
                .httpOnly(true).secure(true).path("/").maxAge(refreshTokenExpiration).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(res);
    }

    @PutMapping("/logout")
    @ApiMessage("Logout Account")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", defaultValue = "invalid") String refreshToken) {
        if (refreshToken.equals("invalid")) {
            throw new BadCredentialsException("Refresh Token Invalid");
        }
        this.userService.handleRefreshTokenWithCondition(refreshToken, "delete");
        return ResponseEntity.ok(null);
    }

    @PostMapping("/logout")
    @ApiMessage("Logout Account")
    public ResponseEntity<Void> logoutWithCookie() {
        var email = SecurityUtils.getCurrentUserLogin().isPresent() ? securityUtils.getCurrentUserLogin().get() : "";
        if (email.equals("")) {
            throw new BadCredentialsException("Account Invalid");
        }
        this.userService.logout(email);
        var cookie = ResponseCookie.from("refresh_token", "").httpOnly(true).secure(true).path("/").maxAge(0).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(null);
    }

    @GetMapping("/account")
    @ApiMessage("Get Account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        return ResponseEntity.ok(this.userService.getAccount());
    }

}
