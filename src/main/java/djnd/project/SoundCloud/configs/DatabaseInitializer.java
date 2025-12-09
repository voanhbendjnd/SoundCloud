package djnd.project.SoundCloud.configs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import djnd.project.SoundCloud.domain.entity.Permission;
import djnd.project.SoundCloud.domain.entity.Role;
import djnd.project.SoundCloud.domain.entity.User;
import djnd.project.SoundCloud.repositories.PermissionRepository;
import djnd.project.SoundCloud.repositories.RoleRepository;
import djnd.project.SoundCloud.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseInitializer implements CommandLineRunner {
    final UserRepository userRepoRepository;
    final PermissionRepository permissionRepository;
    final RoleRepository roleRepository;
    final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserRepository userRepository, PermissionRepository permissionRepository,
            RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepoRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE <<<");
        Long permissionCnt = this.permissionRepository.count();
        Long userCnt = this.userRepoRepository.count();
        Long roleCnt = this.roleRepository.count();
        if (permissionCnt == 0) {
            List<Permission> permissionList = new ArrayList<>();
            permissionList.add(new Permission("Create new a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            permissionList.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            permissionList
                    .add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            permissionList.add(new Permission("Get a permission", "/api/v1/permissions{id}", "GET", "PERMISSIONS"));
            permissionList.add(new Permission("Fetch all permission", "/api/v1/permissions", "GET", "PERMISSIONS"));

            permissionList.add(new Permission("Create new a role", "/api/v1/roles", "POST", "ROLES"));
            permissionList.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
            permissionList.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            permissionList.add(new Permission("Get a role", "/api/v1/roles/{id}", "GET", "ROLES"));
            permissionList.add(new Permission("Fetch all role", "/api/v1/roles", "GET", "ROLES"));

            permissionList.add(new Permission("Create new a user", "/api/v1/users", "POST", "USERS"));
            permissionList.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            permissionList.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            permissionList.add(new Permission("Get a user", "/api/v1/users/{id}", "GET", "USERS"));
            permissionList.add(new Permission("Fetch all user", "/api/v1/users", "GET", "USERS"));
            this.permissionRepository.saveAll(permissionList);
        }
        if (roleCnt == 0) {
            var permissions = this.permissionRepository.findAll();
            var role = new Role();
            role.setName("SUPER_ADMIN");
            role.setDescription("SUPER ADMIN HAS FULL PERMISSIONS");
            role.setPermissions(permissions);
            this.roleRepository.save(role);
        }
        if (userCnt == 0) {
            User admin = new User();
            admin.setName("ADMIN");
            admin.setEmail("admin@gmail.com");

            admin.setRole(this.roleRepository.findByName("SUPER_ADMIN"));
            admin.setPassword(this.passwordEncoder.encode("123456"));
            this.userRepoRepository.save(admin);
        }
        if (permissionCnt != 0 && roleCnt != 0 && userCnt != 0) {
            System.out.println(">>> SKIP PROCESSING INITIALIER <<<");
        } else {
            System.out.println(">>> INIT DATABASE SUCCESSFULL");
        }
    }
}
