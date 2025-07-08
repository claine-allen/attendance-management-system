package com.example.main.mapper;
import org.springframework.stereotype.Component;

import com.example.main.dto.StudentDTO;
import com.example.main.model.Student;

/**
 * Mapper class for converting between Student entity and Student DTO.
 */
@Component
public class StudentMapper {

    private final UserMapper userMapper;

    public StudentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Converts a Student entity to a StudentDTO.
     * @param student The Student entity.
     * @return The corresponding StudentDTO.
     */
    public StudentDTO toStudentDTO(Student student) {
        if (student == null) {
            return null;
        }
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setRollNumber(student.getRollNumber());
        dto.setBatchYear(student.getBatchYear());
        dto.setSection(student.getSection());
        if (student.getUser() != null) {
            dto.setUserId(student.getUser().getId());
            dto.setUser(userMapper.toUserDTO(student.getUser())); // Map the associated User
        }
        if (student.getDepartment() != null) {
            dto.setDepartmentId(student.getDepartment().getId());
            dto.setDepartmentName(student.getDepartment().getName());
        }
        return dto;
    }

    /**
     * Converts a StudentDTO to a Student entity.
     * Note: The User and Department objects need to be fetched and set in the service layer,
     * as the DTO only contains their IDs.
     * @param dto The StudentDTO.
     * @return The corresponding Student entity (with user and department set to null initially).
     */
    public Student toStudent(StudentDTO dto) {
        if (dto == null) {
            return null;
        }
        Student student = new Student();
        student.setId(dto.getId()); // ID might be null for new entities
        student.setRollNumber(dto.getRollNumber());
        student.setBatchYear(dto.getBatchYear());
        student.setSection(dto.getSection());
        // User and Department will be set in the service layer after fetching them by ID
        return student;
    }
}
