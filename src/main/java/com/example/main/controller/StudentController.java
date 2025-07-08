package com.example.main.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.main.dto.StudentDTO;
import com.example.main.dto.UserRegisterRequest;
import com.example.main.service.StudentService;

import java.util.List;

/**
 * REST Controller for managing Student resources.
 * Creation, update, deletion requires ADMIN. Retrieval is for ADMIN and STUDENT.
 */
@RestController
@RequestMapping("/api/v1/students") // Base path for student endpoints
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Creates a new student, including their associated user account.
     * Requires ADMIN role.
     * @param studentDTO The DTO containing student details.
     * @return ResponseEntity with the created StudentDTO and HTTP status 201.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(
    		@Valid @RequestBody StudentDTO studentDTO,
    		@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        StudentDTO createdStudent = studentService.createStudent(studentDTO,userRegisterRequest);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    /**
     * Retrieves a student by their ID.
     * Requires ADMIN or STUDENT role. Students can only view their own profile.
     * @param id The ID of the student.
     * @return ResponseEntity with the StudentDTO if found, and HTTP status 200.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        // Implement logic to ensure students can only access their own profile if not ADMIN
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    /**
     * Retrieves all students, or students filtered by department, batch year, or section.
     * Requires ADMIN or STUDENT role. Students can only see relevant groups (e.g., their own batch/department).
     * @param departmentId (Optional) Filter students by department ID.
     * @param batchYear (Optional) Filter students by batch year.
     * @param section (Optional) Filter students by section.
     * @return ResponseEntity with a list of StudentDTOs and HTTP status 200.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer batchYear,
            @RequestParam(required = false) String section) {
        List<StudentDTO> students;
        if (departmentId != null) {
            students = studentService.getStudentsByDepartment(departmentId);
        } else if (batchYear != null) {
            students = studentService.getStudentsByBatchYearAndSection(batchYear, section);
        } else {
            students = studentService.getAllStudents();
        }
        return ResponseEntity.ok(students);
    }

    /**
     * Updates an existing student's details.
     * Requires ADMIN role.
     * @param id The ID of the student to update.
     * @param studentDTO The DTO containing updated student details.
     * @return ResponseEntity with the updated StudentDTO and HTTP status 200.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    /**
     * Deletes a student by their ID.
     * Requires ADMIN role.
     * @param id The ID of the student to delete.
     * @return ResponseEntity with HTTP status 204 (No Content) on successful deletion.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
