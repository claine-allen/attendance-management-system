package com.example.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Teacher data transfer.
 * Includes user ID and department ID for association.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {
    private Long id;
    private Long userId; // The associated user ID
    private UserDTO user; // For displaying user details in response

    @NotBlank(message = "Employee ID cannot be blank")
    @Size(max = 50, message = "Employee ID cannot exceed 50 characters")
    private String employeeId;

    @NotNull(message = "Department ID cannot be null")
    private Long departmentId; // Foreign key to Department
    private String departmentName; // For display purposes in response
}
