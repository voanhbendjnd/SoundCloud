package djnd.project.SoundCloud.domain.request.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    @NotBlank(message = "Name cannot be empty!")
    private String name;
    @Email(message = "Email in correct format!")
    @NotBlank(message = "Email cannot be empty!")
    private String email;
    @NotBlank(message = "Password cannot be empty!")
    @Size(min = 6, message = "Password must be least 6 character!")
    private String password;
}
