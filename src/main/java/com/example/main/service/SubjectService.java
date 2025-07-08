package com.example.main.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.main.dto.SubjectDTO;
import com.example.main.exception.DuplicateEntryException;
import com.example.main.exception.ResourceNotFoundException;
import com.example.main.mapper.SubjectMapper;
import com.example.main.model.Department;
import com.example.main.model.Subject;
import com.example.main.repository.SubjectRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing Subject entities.
 * Handles business logic related to subject creation, retrieval, and updates.
 */
@Service
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final DepartmentService departmentService; // Inject DepartmentService to fetch Department entity
    private final SubjectMapper subjectMapper;

    public SubjectService(SubjectRepository subjectRepository, DepartmentService departmentService, SubjectMapper subjectMapper) {
        this.subjectRepository = subjectRepository;
        this.departmentService = departmentService;
        this.subjectMapper = subjectMapper;
    }

    /**
     * Creates a new subject.
     * @param subjectDTO The DTO containing subject details.
     * @return The created SubjectDTO.
     * @throws DuplicateEntryException if a subject with the same code already exists.
     * @throws ResourceNotFoundException if the associated department is not found.
     */
    public SubjectDTO createSubject(SubjectDTO subjectDTO) {
        if (subjectRepository.existsByCode(subjectDTO.getCode())) {
            throw new DuplicateEntryException("Subject with code '" + subjectDTO.getCode() + "' already exists.");
        }

        Department department = departmentService.getDepartmentEntityById(subjectDTO.getDepartmentId());

        Subject subject = subjectMapper.toSubject(subjectDTO);
        subject.setDepartment(department); // Set the actual Department entity

        Subject savedSubject = subjectRepository.save(subject);
        return subjectMapper.toSubjectDTO(savedSubject);
    }

    /**
     * Retrieves a subject by its ID.
     * @param id The ID of the subject.
     * @return The SubjectDTO if found.
     * @throws ResourceNotFoundException if the subject is not found.
     */
    public SubjectDTO getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
        return subjectMapper.toSubjectDTO(subject);
    }

    /**
     * Retrieves a subject entity by its ID. Used internally by other services.
     * @param id The ID of the subject.
     * @return The Subject entity if found.
     * @throws ResourceNotFoundException if the subject is not found.
     */
    public Subject getSubjectEntityById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
    }

    /**
     * Retrieves all subjects as DTOs. Used typically for exposing data via API endpoints.
     * @return A list of all SubjectDTOs.
     */
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toSubjectDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all subjects as entities. Used for internal service-to-service communication
     * when the full entity object is required.
     * @return A list of all Subject entities.
     */
    public List<Subject> getAllSubjectEntities() { // NEW METHOD ADDED
        return subjectRepository.findAll();
    }

    /**
     * Retrieves subjects by department.
     * @param departmentId The ID of the department.
     * @return A list of SubjectDTOs for the specified department.
     * @throws ResourceNotFoundException if the department is not found.
     */
    public List<SubjectDTO> getSubjectsByDepartment(Long departmentId) {
        Department department = departmentService.getDepartmentEntityById(departmentId);
        return subjectRepository.findByDepartment(department).stream()
                .map(subjectMapper::toSubjectDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing subject.
     * @param id The ID of the subject to update.
     * @param subjectDTO The DTO containing updated subject details.
     * @return The updated SubjectDTO.
     * @throws ResourceNotFoundException if the subject or associated department is not found.
     * @throws DuplicateEntryException if an attempt is made to change code to an existing one.
     */
    public SubjectDTO updateSubject(Long id, SubjectDTO subjectDTO) {
        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));

        // Check for duplicate code if changed
        if (!existingSubject.getCode().equalsIgnoreCase(subjectDTO.getCode()) && subjectRepository.existsByCode(subjectDTO.getCode())) {
            throw new DuplicateEntryException("Subject with code '" + subjectDTO.getCode() + "' already exists.");
        }

        Department department = departmentService.getDepartmentEntityById(subjectDTO.getDepartmentId());

        existingSubject.setName(subjectDTO.getName());
        existingSubject.setCode(subjectDTO.getCode());
        existingSubject.setDepartment(department); // Update associated Department

        Subject updatedSubject = subjectRepository.save(existingSubject);
        return subjectMapper.toSubjectDTO(updatedSubject);
    }

    /**
     * Deletes a subject by its ID.
     * NOTE: Consider implementing checks for associated lectures before allowing deletion,
     * or cascade deletion if appropriate for business rules.
     * For now, cascade is configured in entities.
     * @param id The ID of the subject to delete.
     * @throws ResourceNotFoundException if the subject is not found.
     */
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found with ID: " + id);
        }
        subjectRepository.deleteById(id);
    }
}