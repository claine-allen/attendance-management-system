package com.example.main.mapper;
import org.springframework.stereotype.Component;

import com.example.main.dto.DepartmentDTO;
import com.example.main.model.Department;

/**
 * Mapper class for converting between Department entity and Department DTO.
 */
@Component
public class DepartmentMapper {

    /**
     * Converts a Department entity to a DepartmentDTO.
     * @param department The Department entity.
     * @return The corresponding DepartmentDTO.
     */
    public DepartmentDTO toDepartmentDTO(Department department) {
        if (department == null) {
            return null;
        }
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCode(department.getCode());
        return dto;
    }

    /**
     * Converts a DepartmentDTO to a Department entity.
     * This method is typically used for creating new departments or updating existing ones.
     * @param dto The DepartmentDTO.
     * @return The corresponding Department entity.
     */
    public Department toDepartment(DepartmentDTO dto) {
        if (dto == null) {
            return null;
        }
        Department department = new Department();
        department.setId(dto.getId()); // ID might be null for new entities
        department.setName(dto.getName());
        department.setCode(dto.getCode());
        return department;
    }
}
