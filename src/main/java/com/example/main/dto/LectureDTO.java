package com.example.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for Lecture data transfer.
 * Includes subject ID and teacher ID for association.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LectureDTO {
	private Long id;

    @NotNull(message = "Subject ID cannot be null")
    private Long subjectId;
    private String subjectName; // For display
    private String subjectCode; // For display

    @NotNull(message = "Teacher ID cannot be null")
    private Long teacherId;
    private String teacherName; // For display

    @NotNull(message = "Lecture date cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lectureDate;

    @NotNull(message = "Start time cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;

    @NotNull(message = "End time cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endTime;

    @NotBlank(message = "Student group cannot be blank")
    private String studentGroup; // E.g., "B.Tech CSE 3rd Sem A"

    private String roomNumber; // Optional
}
