package djnd.project.SoundCloud.utils.convert;

import djnd.project.SoundCloud.domain.entity.User;
import djnd.project.SoundCloud.domain.request.users.UserDTO;
import djnd.project.SoundCloud.domain.response.users.ResUser;

public class convertUtils {
    public static ResUser toResUser(User user) {
        var res = new ResUser();
        res.setId(user.getId());
        res.setCreatedAt(user.getCreatedAt());
        res.setCreatedBy(user.getCreatedBy());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedBy(user.getCreatedBy());
        return res;
    }

    public static User toUser(UserDTO dto) {
        var user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        return user;
    }
}
