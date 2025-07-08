package com.example.main.mapper;
import org.springframework.stereotype.Component;

import com.example.main.dto.SubjectDTO;
import com.example.main.model.Subject;

/**
 * Mapper class for converting between Subject entity and Subject DTO.
 */
@Component
public class SubjectMapper {

    /**
     * Converts a Subject entity to a SubjectDTO.
     * @param subject The Subject entity.
     * @return The corresponding SubjectDTO.
     */
    public SubjectDTO toSubjectDTO(Subject subject) {
        if (subject == null) {
            return null;
        }
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.setCode(subject.getCode());
        if (subject.getDepartment() != null) {
            dto.setDepartmentId(subject.getDepartment().getId());
            dto.setDepartmentName(subject.getDepartment().getName());
        }
        return dto;
    }

    /**
     * Converts a SubjectDTO to a Subject entity.
     * Note: The Department object for the subject needs to be fetched and set in the service layer,
     * as the DTO only contains the departmentId.
     * @param dto The SubjectDTO.
     * @return The corresponding Subject entity (with department set to null initially).
     */
    public Subject toSubject(SubjectDTO dto) {
        if (dto == null) {
            return null;
        }
        Subject subject = new Subject();
        subject.setId(dto.getId()); // ID might be null for new entities
        subject.setName(dto.getName());
        subject.setCode(dto.getCode());
        // Department will be set in the service layer after fetching it by ID
        return subject;
    }
}
