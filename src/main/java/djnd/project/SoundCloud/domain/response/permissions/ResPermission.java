package djnd.project.SoundCloud.domain.response.permissions;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResPermission {
    Long id;
    String name;
    String method;
    String module;
    String createdBy, updatedBy;
    Instant createdAt, updatedAt;
}
