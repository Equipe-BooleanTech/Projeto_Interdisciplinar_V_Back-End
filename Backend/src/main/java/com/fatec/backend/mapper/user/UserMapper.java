package com.fatec.backend.mapper.user;

import com.fatec.backend.DTO.user.UserDTO;
import com.fatec.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO ToUserDTO(User user);
    User ToUser(UserDTO userDTO);
}
