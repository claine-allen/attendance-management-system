package com.example.main.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.main.dto.DepartmentDTO;
import com.example.main.exception.DuplicateEntryException;
import com.example.main.exception.ResourceNotFoundException;
import com.example.main.mapper.DepartmentMapper;
import com.example.main.model.Department;
import com.example.main.repository.DepartmentRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing Department entities.
 * Handles business logic related to department creation, retrieval, and updates.
 */
@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    /**
     * Creates a new department.
     * @param departmentDTO The DTO containing department details.
     * @return The created DepartmentDTO.
     * @throws DuplicateEntryException if a department with the same name or code already exists.
     */
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        if (departmentRepository.existsByName(departmentDTO.getName())) {
            throw new DuplicateEntryException("Department with name '" + departmentDTO.getName() + "' already exists.");
        }
        if (departmentRepository.existsByCode(departmentDTO.getCode())) {
            throw new DuplicateEntryException("Department with code '" + departmentDTO.getCode() + "' already exists.");
        }

        Department department = departmentMapper.toDepartment(departmentDTO);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toDepartmentDTO(savedDepartment);
    }

    /**
     * Retrieves a department by its ID.
     * @param id The ID of the department.
     * @return The DepartmentDTO if found.
     * @throws ResourceNotFoundException if the department is not found.
     */
    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
        return departmentMapper.toDepartmentDTO(department);
    }

    /**
     * Retrieves a department entity by its ID. Used internally by other services.
     * @param id The ID of the department.
     * @return The Department entity if found.
     * @throws ResourceNotFoundException if the department is not found.
     */
    public Department getDepartmentEntityById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
    }

    /**
     * Retrieves all departments.
     * @return A list of all DepartmentDTOs.
     */
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toDepartmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing department.
     * @param id The ID of the department to update.
     * @param departmentDTO The DTO containing updated department details.
     * @return The updated DepartmentDTO.
     * @throws ResourceNotFoundException if the department is not found.
     * @throws DuplicateEntryException if an attempt is made to change name/code to an existing one.
     */
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        // Check for duplicate name if changed
        if (!existingDepartment.getName().equalsIgnoreCase(departmentDTO.getName()) && departmentRepository.existsByName(departmentDTO.getName())) {
            throw new DuplicateEntryException("Department with name '" + departmentDTO.getName() + "' already exists.");
        }
        // Check for duplicate code if changed
        if (!existingDepartment.getCode().equalsIgnoreCase(departmentDTO.getCode()) && departmentRepository.existsByCode(departmentDTO.getCode())) {
            throw new DuplicateEntryException("Department with code '" + departmentDTO.getCode() + "' already exists.");
        }

        existingDepartment.setName(departmentDTO.getName());
        existingDepartment.setCode(departmentDTO.getCode());

        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return departmentMapper.toDepartmentDTO(updatedDepartment);
    }

    /**
     * Deletes a department by its ID.
     * NOTE: Consider implementing checks for associated subjects, teachers, or students
     * before allowing deletion, or cascade deletion if appropriate for business rules.
     * For now, cascade is configured in entities.
     * @param id The ID of the department to delete.
     * @throws ResourceNotFoundException if the department is not found.
     */
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with ID: " + id);
        }
        departmentRepository.deleteById(id);
    }
}
