package co.com.nequi.webclient.mapper;

import co.com.nequi.webclient.response.UserResponse;
import co.com.nequi.model.user.User;

public class UserResponseMapper {
    private UserResponseMapper() {}

    public static User toUser(UserResponse.Data data) {
        return new User(
            data.getId(),
            data.getFirstName(),
            data.getLastName(),
            data.getEmail()
        );
    }
}
