package djnd.project.SoundCloud.services;

import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import djnd.project.SoundCloud.domain.entity.User;
import djnd.project.SoundCloud.domain.request.users.UserDTO;
import djnd.project.SoundCloud.domain.request.users.UserUpdateDTO;
import djnd.project.SoundCloud.domain.response.ResultPaginationDTO;
import djnd.project.SoundCloud.domain.response.users.ResUser;
import djnd.project.SoundCloud.repositories.UserRepository;
import djnd.project.SoundCloud.utils.convert.convertUtils;
import djnd.project.SoundCloud.utils.error.DuplicateResourceException;
import djnd.project.SoundCloud.utils.error.ResourceNotFoundException;

@Service
public class UserService {
    private final UserRepository userRepository;
    // private final UserMapper userMapper;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        // this.userMapper = userMapper;
    }

    public Long create(UserDTO dto) {
        if (this.userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email", dto.getEmail());
        }
        var user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        var lastUser = this.userRepository.save(user);
        return lastUser.getId();
    }

    public ResUser updatePartial(UserUpdateDTO dto) {
        var user = this.userRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.getId() + ""));
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            user.setEmail(dto.getEmail());
        }
        var lastUser = this.userRepository.save(user);
        return convertUtils.toResUser(lastUser);

    }

    public ResUser update(UserUpdateDTO dto) {
        var user = this.userRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ID", dto.getId() + ""));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        var lastUser = this.userRepository.save(user);
        return convertUtils.toResUser(lastUser);
    }

    public ResUser findById(long id) {
        var user = this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ID", id + ""));
        return convertUtils.toResUser(user);
    }

    public void deleteById(long id) {
        var user = this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ID", ""));
        this.userRepository.delete(user);
    }

    public ResultPaginationDTO fetchAll(Specification<User> spec, Pageable pageable) {
        var page = this.userRepository.findAll(spec, pageable);
        var res = new ResultPaginationDTO();
        var mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        res.setMeta(mt);
        res.setResult(page.getContent().stream().map(u -> {
            var resUser = convertUtils.toResUser(u);
            return resUser;
        }).collect(Collectors.toList()));
        return res;
    }

}
