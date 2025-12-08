package djnd.project.SoundCloud.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import djnd.project.SoundCloud.domain.request.users.UserRequest;
import djnd.project.SoundCloud.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<Long> create(@RequestBody @Valid UserRequest request) throws MethodArgumentNotValidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.create(request));
    }
    public ResponseEntity<>

}
