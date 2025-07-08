package com.example.main.mapper;
import org.springframework.stereotype.Component;

import com.example.main.dto.UserDTO;
import com.example.main.dto.UserRegisterRequest;
import com.example.main.model.User;

/**
 * Mapper class for converting between User entity and User DTOs.
 */
@Component
public class UserMapper {

    /**
     * Converts a User entity to a UserDTO.
     * @param user The User entity.
     * @return The corresponding UserDTO.
     */
    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setActive(user.isActive());
        return dto;
    }

    /**
     * Converts a UserRegisterRequest DTO to a User entity.
     * Note: Password hashing should happen in the service layer, not here.
     * @param request The UserRegisterRequest DTO.
     * @return The corresponding User entity.
     */
    public User toUser(UserRegisterRequest request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Password will be hashed in service
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(true); // Default active on registration
        return user;
    }
}
