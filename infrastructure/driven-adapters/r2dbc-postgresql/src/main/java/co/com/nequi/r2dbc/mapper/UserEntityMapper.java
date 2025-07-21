package co.com.nequi.r2dbc.mapper;

import co.com.nequi.model.user.User;
import co.com.nequi.r2dbc.entity.UserEntity;

public class UserEntityMapper {
    private UserEntityMapper() {}

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    public static User toModel(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .build();
    }
}
