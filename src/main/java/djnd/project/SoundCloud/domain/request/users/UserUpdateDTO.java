package djnd.project.SoundCloud.domain.request.users;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    @NotNull(message = "ID cannot be Null!")
    private Long id;
    private String name;
    private String email;
}
