package com.example.main.controller;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.main.dto.SubjectDTO;
import com.example.main.service.SubjectService;

import java.util.List;

/**
 * REST Controller for managing Subject resources.
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/subjects") // Base path for subject endpoints
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    /**
     * Creates a new subject.
     * Requires ADMIN role.
     * @param subjectDTO The DTO containing subject details.
     * @return ResponseEntity with the created SubjectDTO and HTTP status 201.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SubjectDTO> createSubject(@Valid @RequestBody SubjectDTO subjectDTO) {
        SubjectDTO createdSubject = subjectService.createSubject(subjectDTO);
        return new ResponseEntity<>(createdSubject, HttpStatus.CREATED);
    }

    /**
     * Retrieves a subject by its ID.
     * Requires ADMIN role.
     * @param id The ID of the subject.
     * @return ResponseEntity with the SubjectDTO if found, and HTTP status 200.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Long id) {
        SubjectDTO subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    /**
     * Retrieves all subjects.
     * Requires ADMIN role.
     * @return ResponseEntity with a list of all SubjectDTOs and HTTP status 200.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects(@RequestParam(required = false) Long departmentId) {
        List<SubjectDTO> subjects;
        if (departmentId != null) {
            subjects = subjectService.getSubjectsByDepartment(departmentId);
        } else {
            subjects = subjectService.getAllSubjects();
        }
        return ResponseEntity.ok(subjects);
    }

    /**
     * Updates an existing subject.
     * Requires ADMIN role.
     * @param id The ID of the subject to update.
     * @param subjectDTO The DTO containing updated subject details.
     * @return ResponseEntity with the updated SubjectDTO and HTTP status 200.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectDTO subjectDTO) {
        SubjectDTO updatedSubject = subjectService.updateSubject(id, subjectDTO);
        return ResponseEntity.ok(updatedSubject);
    }

    /**
     * Deletes a subject by its ID.
     * Requires ADMIN role.
     * @param id The ID of the subject to delete.
     * @return ResponseEntity with HTTP status 204 (No Content) on successful deletion.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
