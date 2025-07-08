package com.example.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for overall student attendance view, including subject-wise summaries.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverallStudentAttendanceDTO {
    private Long studentId;
    private UserDTO user;
    private String rollNumber;
    private String departmentName;
    private Integer batchYear;
    private String section;
    private double overallPercentage;
    private List<StudentAttendanceSummaryDTO> subjectSummaries;
}
