package com.courtreservation.authentication.mapper;

import com.courtreservation.authentication.dto.LoginResponse;
import com.courtreservation.authentication.dto.RegisterRequest;
import com.courtreservation.authentication.dto.UserResponse;
import com.courtreservation.authentication.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    User toUser(RegisterRequest request);

    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponses(List<User> users);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "userMail", source = "user.userMail")
    @Mapping(target = "userRole", source = "user.userRole")
    LoginResponse toLoginResponse(User user, String token);
}
