package com.example.main.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.main.dto.DepartmentDTO;
import com.example.main.service.DepartmentService;

import java.util.List;

/**
 * REST Controller for managing Department resources.
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/departments") // Base path for department endpoints
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * Creates a new department.
     * Requires ADMIN role.
     * @param departmentDTO The DTO containing department details.
     * @return ResponseEntity with the created DepartmentDTO and HTTP status 201.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }

    /**
     * Retrieves a department by its ID.
     * Requires ADMIN role.
     * @param id The ID of the department.
     * @return ResponseEntity with the DepartmentDTO if found, and HTTP status 200.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        DepartmentDTO department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    /**
     * Retrieves all departments.
     * Requires ADMIN role.
     * @return ResponseEntity with a list of all DepartmentDTOs and HTTP status 200.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    /**
     * Updates an existing department.
     * Requires ADMIN role.
     * @param id The ID of the department to update.
     * @param departmentDTO The DTO containing updated department details.
     * @return ResponseEntity with the updated DepartmentDTO and HTTP status 200.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO updatedDepartment = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(updatedDepartment);
    }

    /**
     * Deletes a department by its ID.
     * Requires ADMIN role.
     * @param id The ID of the department to delete.
     * @return ResponseEntity with HTTP status 204 (No Content) on successful deletion.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
