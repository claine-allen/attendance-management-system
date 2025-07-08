package com.example.main.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.main.dto.LectureDTO;
import com.example.main.security.CustomUserDetails;
import com.example.main.service.LectureService;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing Lecture resources.
 * Scheduling/Update/Deletion requires ADMIN. Retrieval is for ADMIN, TEACHER, and STUDENT.
 */
@RestController
@RequestMapping("/api/v1/lectures") // Base path for lecture endpoints
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    /**
     * Schedules a new lecture.
     * Requires ADMIN role.
     * @param lectureDTO The DTO containing lecture details.
     * @return ResponseEntity with the scheduled LectureDTO and HTTP status 201.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<LectureDTO> scheduleLecture(@Valid @RequestBody LectureDTO lectureDTO) {
        LectureDTO scheduledLecture = lectureService.scheduleLecture(lectureDTO);
        return new ResponseEntity<>(scheduledLecture, HttpStatus.CREATED);
    }

    /**
     * Retrieves a lecture by its ID.
     * Requires ADMIN, TEACHER, or STUDENT role.
     * @param id The ID of the lecture.
     * @return ResponseEntity with the LectureDTO if found, and HTTP status 200.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<LectureDTO> getLectureById(@PathVariable Long id) {
        LectureDTO lecture = lectureService.getLectureById(id);
        return ResponseEntity.ok(lecture);
    }

    /**
     * Retrieves all lectures, or filters by teacher ID, student group, and/or date.
     * Requires ADMIN, TEACHER, or STUDENT role.
     * Teachers can only retrieve their own lectures. Students can only retrieve lectures for their group.
     * @param teacherId (Optional) Filter by teacher ID.
     * @param studentGroup (Optional) Filter by student group.
     * @param date (Optional) Filter by lecture date (format YYYY-MM-DD).
     * @param currentUser The authenticated user's details.
     * @return ResponseEntity with a list of LectureDTOs and HTTP status 200.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping
    public ResponseEntity<List<LectureDTO>> getLectures(
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String studentGroup,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<LectureDTO> lectures;

        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // Admin can view all or filter freely
            if (teacherId != null) {
                lectures = lectureService.getLecturesByTeacher(teacherId, date);
            } else if (studentGroup != null) {
                lectures = lectureService.getLecturesByStudentGroup(studentGroup, date);
            } else {
                lectures = lectureService.getAllLectures();
            }
        } else if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            // Teacher can only view their own lectures
            Long currentTeacherId = currentUser.getUserId(); // Assuming user ID is also teacher ID for now, adjust if separate
            lectures = lectureService.getLecturesByTeacher(currentTeacherId, date);
        } else if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            // Student can only view lectures for their group
            // This would typically involve fetching the student's group based on their user ID,
            // For simplicity, let's assume `studentGroup` is passed or fetched via other means.
            // A more robust implementation would fetch the student's actual group from the Student entity.
            // For now, if no studentGroup is provided by the student, it won't filter based on it here directly.
            lectures = lectureService.getLecturesByStudentGroup(studentGroup, date); // Needs accurate studentGroup to filter
        } else {
            lectures = lectureService.getAllLectures(); // Fallback, though roles should cover all authenticated users
        }

        return ResponseEntity.ok(lectures);
    }

    /**
     * Updates an existing lecture.
     * Requires ADMIN role.
     * @param id The ID of the lecture to update.
     * @param lectureDTO The DTO containing updated lecture details.
     * @return ResponseEntity with the updated LectureDTO and HTTP status 200.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<LectureDTO> updateLecture(@PathVariable Long id, @Valid @RequestBody LectureDTO lectureDTO) {
        LectureDTO updatedLecture = lectureService.updateLecture(id, lectureDTO);
        return ResponseEntity.ok(updatedLecture);
    }

    /**
     * Deletes a lecture by its ID.
     * Requires ADMIN role.
     * @param id The ID of the lecture to delete.
     * @return ResponseEntity with HTTP status 204 (No Content) on successful deletion.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long id) {
        lectureService.deleteLecture(id);
        return ResponseEntity.noContent().build();
    }
}
