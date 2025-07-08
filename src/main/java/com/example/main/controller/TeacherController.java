package com.example.main.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.main.dto.TeacherDTO;
import com.example.main.dto.UserRegisterRequest;
import com.example.main.service.TeacherService;

import java.util.List;

/**
 * REST Controller for managing Teacher resources.
 * Creation, update, deletion requires ADMIN. Retrieval is for ADMIN and TEACHER.
 */
@RestController
@RequestMapping("/api/v1/teachers") // Base path for teacher endpoints
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * Creates a new teacher, including their associated user account.
     * Requires ADMIN role.
     * @param teacherDTO The DTO containing teacher details.
     * @return ResponseEntity with the created TeacherDTO and HTTP status 201.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TeacherDTO> createTeacher(
    		@Valid @RequestBody TeacherDTO teacherDTO,
    		@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        TeacherDTO createdTeacher = teacherService.createTeacher(teacherDTO,userRegisterRequest);
        return new ResponseEntity<>(createdTeacher, HttpStatus.CREATED);
    }

    /**
     * Retrieves a teacher by their ID.
     * Requires ADMIN or TEACHER role. Teachers can only view their own profile.
     * @param id The ID of the teacher.
     * @return ResponseEntity with the TeacherDTO if found, and HTTP status 200.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable Long id) {
        // Implement logic to ensure teachers can only access their own profile if not ADMIN
        // This is handled by @PreAuthorize and then more granularly in the service/controller if needed
        TeacherDTO teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    /**
     * Retrieves all teachers, or teachers filtered by department.
     * Requires ADMIN or TEACHER role.
     * @param departmentId (Optional) Filter teachers by department ID.
     * @return ResponseEntity with a list of TeacherDTOs and HTTP status 200.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping
    public ResponseEntity<List<TeacherDTO>> getAllTeachers(@RequestParam(required = false) Long departmentId) {
        List<TeacherDTO> teachers;
        if (departmentId != null) {
            teachers = teacherService.getTeachersByDepartment(departmentId);
        } else {
            teachers = teacherService.getAllTeachers();
        }
        return ResponseEntity.ok(teachers);
    }

    /**
     * Updates an existing teacher's details.
     * Requires ADMIN role.
     * @param id The ID of the teacher to update.
     * @param teacherDTO The DTO containing updated teacher details.
     * @return ResponseEntity with the updated TeacherDTO and HTTP status 200.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherDTO teacherDTO) {
        TeacherDTO updatedTeacher = teacherService.updateTeacher(id, teacherDTO);
        return ResponseEntity.ok(updatedTeacher);
    }

    /**
     * Deletes a teacher by their ID.
     * Requires ADMIN role.
     * @param id The ID of the teacher to delete.
     * @return ResponseEntity with HTTP status 204 (No Content) on successful deletion.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
