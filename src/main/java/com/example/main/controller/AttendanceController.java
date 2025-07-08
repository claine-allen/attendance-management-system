package com.example.main.controller;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.main.dto.AttendanceRecordDTO;
import com.example.main.dto.BulkMarkAttendanceRequest;
import com.example.main.dto.OverallStudentAttendanceDTO;
import com.example.main.model.AttendanceStatus;
import com.example.main.security.CustomUserDetails;
import com.example.main.service.AttendanceService;
import com.example.main.service.StudentService;
import com.example.main.service.TeacherService;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing Attendance records.
 */
@RestController
@RequestMapping("/api/v1/attendance") // Base path for attendance endpoints
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final StudentService studentService; // Needed for student-specific attendance logic
    private final TeacherService teacherService; // Needed for teacher-specific attendance logic

    public AttendanceController(AttendanceService attendanceService, StudentService studentService, TeacherService teacherService) {
        this.attendanceService = attendanceService;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    /**
     * Marks attendance for multiple students in a specific lecture.
     * Requires TEACHER or ADMIN role. Teachers can only mark attendance for their own lectures.
     * @param request The DTO containing lecture ID and a list of student attendance statuses.
     * @param currentUser The authenticated user's details.
     * @return ResponseEntity with a list of marked AttendanceRecordDTOs and HTTP status 200.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping("/mark")
    public ResponseEntity<List<AttendanceRecordDTO>> markBulkAttendance(
            @Valid @RequestBody BulkMarkAttendanceRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        // In a more robust system, we would verify that the current teacher
        // is indeed authorized to mark attendance for the specified lecture.
        // For simplicity now, we rely on @PreAuthorize for role and assume
        // the teacherId passed to service is the currently authenticated teacher's ID.
        Long markingTeacherId = currentUser.getUserId();
        List<AttendanceRecordDTO> markedRecords = attendanceService.markBulkAttendance(request, markingTeacherId);
        return ResponseEntity.ok(markedRecords);
    }

    /**
     * Updates a single attendance record.
     * Requires TEACHER or ADMIN role. Teachers can only update records they marked or for their lectures.
     * @param recordId The ID of the attendance record to update.
     * @param updatedStatus The new attendance status.
     * @param currentUser The authenticated user's details.
     * @return ResponseEntity with the updated AttendanceRecordDTO and HTTP status 200.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping("/{recordId}")
    public ResponseEntity<AttendanceRecordDTO> updateAttendanceRecord(
            @PathVariable Long recordId,
            @RequestParam AttendanceStatus updatedStatus,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        // Similar to marking, ensure authorization for the teacher to update this specific record.
        Long updatingTeacherId = currentUser.getUserId();
        AttendanceRecordDTO updatedRecord = attendanceService.updateAttendanceRecord(recordId, updatedStatus, updatingTeacherId);
        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * Retrieves attendance records for a specific lecture.
     * Requires ADMIN or TEACHER role.
     * @param lectureId The ID of the lecture.
     * @return ResponseEntity with a list of AttendanceRecordDTOs and HTTP status 200.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<List<AttendanceRecordDTO>> getAttendanceByLecture(@PathVariable Long lectureId) {
        List<AttendanceRecordDTO> attendanceRecords = attendanceService.getAttendanceByLecture(lectureId);
        return ResponseEntity.ok(attendanceRecords);
    }

    /**
     * Retrieves attendance records for a specific student, optionally within a date range.
     * Requires ADMIN or STUDENT role. Students can only view their own attendance.
     * @param studentId The ID of the student.
     * @param startDate (Optional) Start date for filtering attendance (YYYY-MM-DD).
     * @param endDate (Optional) End date for filtering attendance (YYYY-MM-DD).
     * @param currentUser The authenticated user's details.
     * @return ResponseEntity with a list of AttendanceRecordDTOs and HTTP status 200.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceRecordDTO>> getAttendanceByStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        // Ensure students can only view their own records
        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")) &&
            !currentUser.getUserId().equals(studentId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Return 403 Forbidden
        }

        List<AttendanceRecordDTO> attendanceRecords = attendanceService.getAttendanceByStudent(studentId, startDate, endDate);
        return ResponseEntity.ok(attendanceRecords);
    }

    /**
     * Retrieves the overall attendance summary for a specific student.
     * Requires ADMIN or STUDENT role. Students can only view their own summary.
     * @param studentId The ID of the student.
     * @param currentUser The authenticated user's details.
     * @return ResponseEntity with the OverallStudentAttendanceDTO and HTTP status 200.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<OverallStudentAttendanceDTO> getStudentOverallAttendanceSummary(
            @PathVariable Long studentId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        // Ensure students can only view their own summary
        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")) &&
            !currentUser.getUserId().equals(studentId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Return 403 Forbidden
        }

        OverallStudentAttendanceDTO summary = attendanceService.getStudentOverallAttendanceSummary(studentId);
        return ResponseEntity.ok(summary);
    }
}
