package com.example.main.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.example.main.model.AttendanceStatus;

/**
 * DTO for Attendance Record data transfer.
 * Used for both request and response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecordDTO {
	   private Long id;

	    @NotNull(message = "Lecture ID cannot be null")
	    private Long lectureId;
	    private LectureDTO lecture; // For displaying lecture details in response

	    @NotNull(message = "Student ID cannot be null")
	    private Long studentId;
	    private StudentDTO student; // For displaying student details in response

	    @NotNull(message = "Attendance status cannot be null")
	    private AttendanceStatus status;

	    private Long markedByTeacherId; // ID of the teacher who marked it
	    private String markedByTeacherName; // Name for display

	    private LocalDateTime markingTimestamp;
}
