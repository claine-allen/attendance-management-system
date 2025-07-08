package com.example.main.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.main.dto.StudentDTO;
import com.example.main.dto.UserDTO;
import com.example.main.dto.UserRegisterRequest;
import com.example.main.exception.DuplicateEntryException;
import com.example.main.exception.ResourceNotFoundException;
import com.example.main.mapper.StudentMapper;
import com.example.main.model.Department;
import com.example.main.model.Role;
import com.example.main.model.Student;
import com.example.main.model.User;
import com.example.main.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing Student entities.
 * Handles business logic related to student creation, retrieval, and updates.
 */
@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserService userService; // Inject UserService to manage User accounts
    private final DepartmentService departmentService; // Inject DepartmentService to fetch Department entity
    private final StudentMapper studentMapper;

    public StudentService(StudentRepository studentRepository, UserService userService, DepartmentService departmentService, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.userService = userService;
        this.departmentService = departmentService;
        this.studentMapper = studentMapper;
    }

    /**
     * Creates a new student, including their associated user account.
     * @param studentDTO The DTO containing student details, including user information.
     * @return The created StudentDTO.
     * @throws DuplicateEntryException if a student with the given roll number or user email already exists.
     * @throws ResourceNotFoundException if the associated department is not found.
     */
    public StudentDTO createStudent(StudentDTO studentDTO, UserRegisterRequest userRegisterRequest) {
        if (studentRepository.existsByRollNumber(studentDTO.getRollNumber())) {
            throw new DuplicateEntryException("Student with roll number '" + studentDTO.getRollNumber() + "' already exists.");
        }

        // Create the associated User account first
     // The userRegisterRequest already contains email, password, etc.
        // The userService.registerUser method will handle hashing the password internally.
        UserDTO createdUserDTO = userService.registerUser(userRegisterRequest); // This returns a UserDTO
        
     // Convert the returned UserDTO to a User entity
        User createdUser = createdUserDTO.toEntity(); // Fixed: Use UserDTO.toEntity()

        Department department = departmentService.getDepartmentEntityById(studentDTO.getDepartmentId());

        Student student = studentMapper.toStudent(studentDTO);
        student.setUser(createdUser); // Link to the newly created user
        student.setDepartment(department); // Set the department

        Student savedStudent = studentRepository.save(student);
        return studentMapper.toStudentDTO(savedStudent);
    }

    /**
     * Retrieves a student by their ID.
     * @param id The ID of the student.
     * @return The StudentDTO if found.
     * @throws ResourceNotFoundException if the student is not found.
     */
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return studentMapper.toStudentDTO(student);
    }

    /**
     * Retrieves a student entity by their ID. Used internally by other services.
     * @param id The ID of the student.
     * @return The Student entity if found.
     * @throws ResourceNotFoundException if the student is not found.
     */
    public Student getStudentEntityById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    /**
     * Retrieves all students.
     * @return A list of all StudentDTOs.
     */
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toStudentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves students by department.
     * @param departmentId The ID of the department.
     * @return A list of StudentDTOs for the specified department.
     * @throws ResourceNotFoundException if the department is not found.
     */
    public List<StudentDTO> getStudentsByDepartment(Long departmentId) {
        Department department = departmentService.getDepartmentEntityById(departmentId);
        return studentRepository.findByDepartment(department).stream()
                .map(studentMapper::toStudentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves students by batch year and section.
     * @param batchYear The batch year.
     * @param section The section (can be null for all sections in a batch).
     * @return A list of StudentDTOs matching the criteria.
     */
    public List<StudentDTO> getStudentsByBatchYearAndSection(Integer batchYear, String section) {
        List<Student> students;
        if (section == null || section.isEmpty()) {
            // Find all students in the batch year, regardless of section
            students = studentRepository.findAll().stream()
                    .filter(s -> s.getBatchYear().equals(batchYear))
                    .collect(Collectors.toList());
        } else {
            students = studentRepository.findByBatchYearAndSection(batchYear, section);
        }
        return students.stream()
                .map(studentMapper::toStudentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing student's details.
     * This method handles updates to roll number, batch year, section, and department,
     * and also delegates user details updates.
     * @param id The ID of the student to update.
     * @param studentDTO The DTO containing updated student details.
     * @return The updated StudentDTO.
     * @throws ResourceNotFoundException if the student, associated user, or department is not found.
     * @throws DuplicateEntryException if an attempt is made to change roll number to an existing one.
     */
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        // Check for duplicate roll number if changed
        if (!existingStudent.getRollNumber().equalsIgnoreCase(studentDTO.getRollNumber()) && studentRepository.existsByRollNumber(studentDTO.getRollNumber())) {
            throw new DuplicateEntryException("Student with roll number '" + studentDTO.getRollNumber() + "' already exists.");
        }

        // Update associated User details
        // This UserDTO (from studentDTO.getUser()) does not contain password, which is appropriate for updates.
        if (studentDTO.getUser() != null) {
            userService.updateUser(existingStudent.getUser().getId(), studentDTO.getUser());
        }

        Department department = departmentService.getDepartmentEntityById(studentDTO.getDepartmentId());

        existingStudent.setRollNumber(studentDTO.getRollNumber());
        existingStudent.setDepartment(department);
        existingStudent.setBatchYear(studentDTO.getBatchYear());
        existingStudent.setSection(studentDTO.getSection());

        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.toStudentDTO(updatedStudent);
    }

    /**
     * Deletes a student by their ID. This will also delete the associated User account.
     * NOTE: Consider implications for historical data (e.g., attendance records) before deleting.
     * For now, cascade deletion is configured.
     * @param id The ID of the student to delete.
     * @throws ResourceNotFoundException if the student is not found.
     */
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        // Delete the associated User first (due to cascade type ALL + orphanRemoval on Student.user)
        studentRepository.delete(student);
    }
}
