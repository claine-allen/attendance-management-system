package com.example.main.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.main.dto.TeacherDTO;
import com.example.main.dto.UserDTO;
import com.example.main.dto.UserRegisterRequest;
import com.example.main.exception.DuplicateEntryException;
import com.example.main.exception.InvalidOperationException;
import com.example.main.exception.ResourceNotFoundException;
import com.example.main.mapper.TeacherMapper;
import com.example.main.model.Department;
import com.example.main.model.Role;
import com.example.main.model.Teacher;
import com.example.main.model.User;
import com.example.main.repository.TeacherRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing Teacher entities.
 * Handles business logic related to teacher creation, retrieval, and updates.
 */
@Service
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserService userService; // Inject UserService to manage User accounts
    private final DepartmentService departmentService; // Inject DepartmentService to fetch Department entity
    private final TeacherMapper teacherMapper;

    public TeacherService(TeacherRepository teacherRepository, UserService userService, DepartmentService departmentService, TeacherMapper teacherMapper) {
        this.teacherRepository = teacherRepository;
        this.userService = userService;
        this.departmentService = departmentService;
        this.teacherMapper = teacherMapper;
    }

    /**
     * Creates a new teacher, including their associated user account.
     * @param teacherDTO The DTO containing teacher details, including user information.
     * @return The created TeacherDTO.
     * @throws DuplicateEntryException if a teacher with the given employee ID or user email already exists.
     * @throws ResourceNotFoundException if the associated department is not found.
     */
    public TeacherDTO createTeacher(TeacherDTO teacherDTO, UserRegisterRequest userRegisterRequest) {
        if (teacherRepository.existsByEmployeeId(teacherDTO.getEmployeeId())) {
            throw new DuplicateEntryException("Teacher with employee ID '" + teacherDTO.getEmployeeId() + "' already exists.");
        }

     // Create the associated User account first
        // Line 55 fix: userRegisterRequest contains the password.
        UserDTO createdUserDTO = userService.registerUser(userRegisterRequest); // This returns UserDTO
        User createdUser = createdUserDTO.toEntity(); // Convert UserDTO to User entity using the method we added

        Department department = departmentService.getDepartmentEntityById(teacherDTO.getDepartmentId());

        Teacher teacher = teacherMapper.toTeacher(teacherDTO);
        teacher.setUser(createdUser); // Link to the newly created user
        teacher.setDepartment(department); // Set the department

        Teacher savedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toTeacherDTO(savedTeacher);
    }


    /**
     * Retrieves a teacher by their ID.
     * @param id The ID of the teacher.
     * @return The TeacherDTO if found.
     * @throws ResourceNotFoundException if the teacher is not found.
     */
    public TeacherDTO getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));
        return teacherMapper.toTeacherDTO(teacher);
    }

    /**
     * Retrieves a teacher entity by their ID. Used internally by other services.
     * @param id The ID of the teacher.
     * @return The Teacher entity if found.
     * @throws ResourceNotFoundException if the teacher is not found.
     */
    public Teacher getTeacherEntityById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));
    }

    /**
     * Retrieves all teachers.
     * @return A list of all TeacherDTOs.
     */
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(teacherMapper::toTeacherDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves teachers by department.
     * @param departmentId The ID of the department.
     * @return A list of TeacherDTOs for the specified department.
     * @throws ResourceNotFoundException if the department is not found.
     */
    public List<TeacherDTO> getTeachersByDepartment(Long departmentId) {
        Department department = departmentService.getDepartmentEntityById(departmentId);
        return teacherRepository.findByDepartment(department).stream()
                .map(teacherMapper::toTeacherDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing teacher's details.
     * This method handles updates to employee ID and department, and also delegates user details updates.
     * @param id The ID of the teacher to update.
     * @param teacherDTO The DTO containing updated teacher details.
     * @return The updated TeacherDTO.
     * @throws ResourceNotFoundException if the teacher, associated user, or department is not found.
     * @throws DuplicateEntryException if an attempt is made to change employee ID to an existing one.
     */
    public TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO) {
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));

        // Check for duplicate employee ID if changed
        if (!existingTeacher.getEmployeeId().equalsIgnoreCase(teacherDTO.getEmployeeId()) && teacherRepository.existsByEmployeeId(teacherDTO.getEmployeeId())) {
            throw new DuplicateEntryException("Teacher with employee ID '" + teacherDTO.getEmployeeId() + "' already exists.");
        }

        // Update associated User details
     // Note: TeacherDTO.getUser() returns a UserDTO, which doesn't have password.
        // This is suitable for updating non-password user details.
        if (teacherDTO.getUser() != null) {
            userService.updateUser(existingTeacher.getUser().getId(), teacherDTO.getUser());
        }

        Department department = departmentService.getDepartmentEntityById(teacherDTO.getDepartmentId());

        existingTeacher.setEmployeeId(teacherDTO.getEmployeeId());
        existingTeacher.setDepartment(department);

        Teacher updatedTeacher = teacherRepository.save(existingTeacher);
        return teacherMapper.toTeacherDTO(updatedTeacher);
    }

    /**
     * Deletes a teacher by their ID. This will also delete the associated User account.
     * NOTE: Consider implications for historical data (e.g., lectures taught) before deleting.
     * For now, cascade deletion is configured.
     * @param id The ID of the teacher to delete.
     * @throws ResourceNotFoundException if the teacher is not found.
     */
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));

        // Delete the associated User first (due to cascade type ALL + orphanRemoval on Teacher.user)
        // Or, more explicitly: userService.deactivateUser(teacher.getUser().getId()); if you prefer soft delete for user
        // For hard delete: userRepository.delete(teacher.getUser());
        teacherRepository.delete(teacher);
    }
}
