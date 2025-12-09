package djnd.project.SoundCloud.domain.entity;

import java.time.Instant;

import djnd.project.SoundCloud.utils.SecurityUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    String name;
    String email;
    String password;
    @Column(name = "session_id")
    String sessionId;
    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;

}
