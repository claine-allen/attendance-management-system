package com.example.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Student data transfer.
 * Includes user ID and department ID for association.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private Long userId; // The associated user ID
    private UserDTO user; // For displaying user details in response

    @NotBlank(message = "Roll number cannot be blank")
    @Size(max = 50, message = "Roll number cannot exceed 50 characters")
    private String rollNumber;

    @NotNull(message = "Department ID cannot be null")
    private Long departmentId; // Foreign key to Department
    private String departmentName; // For display purposes in response

    @NotNull(message = "Batch year cannot be null")
    @Min(value = 1900, message = "Batch year must be a valid year") // Example: Assuming college started after 1900
    private Integer batchYear;

    @Size(max = 10, message = "Section cannot exceed 10 characters")
    private String section; // Optional
}
