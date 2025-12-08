package djnd.project.SoundCloud.services;

import org.springframework.stereotype.Service;

import djnd.project.SoundCloud.domain.entity.User;
import djnd.project.SoundCloud.domain.request.users.UserRequest;
import djnd.project.SoundCloud.repositories.UserRepository;
import djnd.project.SoundCloud.utils.error.DuplicateResourceException;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long create(UserRequest request) {
        if (this.userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email", request.getEmail());
        }
        var user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(request.getPassword());
        var lastUser = this.userRepository.save(user);
        return lastUser.getId();
    }

    public UserRepository changeUser() {

    }
}
