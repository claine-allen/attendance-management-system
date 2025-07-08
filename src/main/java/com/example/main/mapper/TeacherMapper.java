package com.example.main.mapper;
import org.springframework.stereotype.Component;

import com.example.main.dto.TeacherDTO;
import com.example.main.model.Teacher;

/**
 * Mapper class for converting between Teacher entity and Teacher DTO.
 */
@Component
public class TeacherMapper {

    private final UserMapper userMapper;

    public TeacherMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Converts a Teacher entity to a TeacherDTO.
     * @param teacher The Teacher entity.
     * @return The corresponding TeacherDTO.
     */
    public TeacherDTO toTeacherDTO(Teacher teacher) {
        if (teacher == null) {
            return null;
        }
        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setEmployeeId(teacher.getEmployeeId());
        if (teacher.getUser() != null) {
            dto.setUserId(teacher.getUser().getId());
            dto.setUser(userMapper.toUserDTO(teacher.getUser())); // Map the associated User
        }
        if (teacher.getDepartment() != null) {
            dto.setDepartmentId(teacher.getDepartment().getId());
            dto.setDepartmentName(teacher.getDepartment().getName());
        }
        return dto;
    }

    /**
     * Converts a TeacherDTO to a Teacher entity.
     * Note: The User and Department objects need to be fetched and set in the service layer,
     * as the DTO only contains their IDs.
     * @param dto The TeacherDTO.
     * @return The corresponding Teacher entity (with user and department set to null initially).
     */
    public Teacher toTeacher(TeacherDTO dto) {
        if (dto == null) {
            return null;
        }
        Teacher teacher = new Teacher();
        teacher.setId(dto.getId()); // ID might be null for new entities
        teacher.setEmployeeId(dto.getEmployeeId());
        // User and Department will be set in the service layer after fetching them by ID
        return teacher;
    }
}
