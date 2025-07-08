package com.example.main.dto;

import com.example.main.model.Department;
import com.example.main.model.Subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Subject data transfer.
 * Includes department ID for association.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private Long id;

    @NotBlank(message = "Subject name cannot be blank")
    private String name;

    @NotBlank(message = "Subject code cannot be blank")
    @Size(max = 50, message = "Subject code cannot exceed 50 characters")
    private String code;

    @NotNull(message = "Department ID cannot be null")
    private Long departmentId; // Foreign key to Department
    private String departmentName; // For display purposes in response

    // --- Conversion Methods ---

    /**
     * Converts this SubjectDTO to a Subject entity.
     * Note: This method assumes the Department entity will be fetched and set separately
     * based on departmentId when persisting to the database, as entities typically
     * hold references to other entities, not just their IDs.
     *
     * @return A Subject entity populated with data from this DTO.
     */
    public Subject toEntity() {
        Subject subject = new Subject();
        subject.setId(this.id); // ID might be null for new subjects, set by DB
        subject.setName(this.name);
        subject.setCode(this.code);

        // For relationships, typically you'd fetch the actual Department entity
        // using the departmentId in your service layer before saving the Subject.
        // This DTO method just sets the ID, assuming the service handles the entity association.
        if (this.departmentId != null) {
            Department department = new Department(); // Create a placeholder Department
            department.setId(this.departmentId);     // Set its ID
            subject.setDepartment(department);       // Associate with the Subject entity
        }

        return subject;
    }

    /**
     * Creates a SubjectDTO from a Subject entity.
     * This is useful when sending Subject data from the backend to the frontend.
     *
     * @param subject The Subject entity to convert.
     * @return A SubjectDTO populated with data from the entity.
     */
    public static SubjectDTO fromEntity(Subject subject) {
        if (subject == null) {
            return null;
        }

        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.setCode(subject.getCode());

        // Populate departmentId and departmentName from the associated Department entity
        if (subject.getDepartment() != null) {
            dto.setDepartmentId(subject.getDepartment().getId());
            dto.setDepartmentName(subject.getDepartment().getName()); // Assuming Department has a getName()
        }

        return dto;
    }
}
