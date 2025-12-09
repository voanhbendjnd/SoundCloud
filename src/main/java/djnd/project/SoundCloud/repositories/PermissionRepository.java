package djnd.project.SoundCloud.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import djnd.project.SoundCloud.domain.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    List<Permission> findByIdIn(List<Long> ids);

    boolean existsByName(String name);

}
