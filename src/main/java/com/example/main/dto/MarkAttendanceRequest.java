package com.example.main.dto;

import com.example.main.model.AttendanceStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for marking or updating a single attendance record.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkAttendanceRequest {
    @NotNull(message = "Student ID cannot be null")
    private Long studentId;

    @NotNull(message = "Attendance status cannot be null")
    private AttendanceStatus status;
}
