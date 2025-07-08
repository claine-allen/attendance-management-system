package com.example.main.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for marking attendance for multiple students in a single lecture.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkMarkAttendanceRequest {
    @NotNull(message = "Lecture ID cannot be null")
    private Long lectureId;

    @NotNull(message = "Attendance records list cannot be null")
    @Size(min = 1, message = "At least one attendance record is required")
    @Valid // Ensures validation is applied to each item in the list
    private List<MarkAttendanceRequest> attendanceRecords;
}
