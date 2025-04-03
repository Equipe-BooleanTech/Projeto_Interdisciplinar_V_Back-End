package com.fatec.backend.mapper.user;

import com.fatec.backend.DTO.user.UserDTO;
import com.fatec.backend.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE=Mapper.getMapper(UserMapper.class);

    UserDTO toUserDTO(User user);
    User toUser(UserDTO userDTO);
}
