package com.example.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for summarizing student attendance for a subject.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceSummaryDTO {
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Long totalLectures;
    private Long lecturesAttended;
    private double attendancePercentage;
}
