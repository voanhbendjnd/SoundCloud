package djnd.project.SoundCloud.domain.response.users;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResUser {
    Long id;
    String email;
    String name;
    String createdBy, updatedBy;
    Instant createdAt, updatedAt;
}
