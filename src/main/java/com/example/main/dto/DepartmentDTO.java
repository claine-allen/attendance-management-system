package com.example.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Department data transfer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private Long id;

    @NotBlank(message = "Department name cannot be blank")
    private String name;

    @NotBlank(message = "Department code cannot be blank")
    @Size(max = 20, message = "Department code cannot exceed 20 characters")
    private String code;
}
