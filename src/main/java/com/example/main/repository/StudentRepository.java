package com.example.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.main.model.Department;
import com.example.main.model.Student;
import com.example.main.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Student entities.
 * Provides standard CRUD operations and custom query methods for Student data.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student,Long>{

    /**
     * Finds a Student by their associated User.
     * @param user The User entity associated with the student.
     * @return An Optional containing the Student if found, or empty.
     */
    Optional<Student> findByUser(User user);

    /**
     * Finds a Student by their roll number.
     * @param rollNumber The roll number of the student.
     * @return An Optional containing the Student if found, or empty.
     */
    Optional<Student> findByRollNumber(String rollNumber);

    /**
     * Finds all Students belonging to a specific Department.
     * @param department The Department entity.
     * @return A list of Students associated with the given department.
     */
    List<Student> findByDepartment(Department department);

    /**
     * Finds all Students in a specific batch year and section.
     * @param batchYear The batch year of the students.
     * @param section The section of the students.
     * @return A list of Students matching the batch year and section.
     */
    List<Student> findByBatchYearAndSection(Integer batchYear, String section);

    /**
     * Checks if a student exists with the given roll number.
     * @param rollNumber The roll number to check.
     * @return True if a student with the roll number exists, false otherwise.
     */
    boolean existsByRollNumber(String rollNumber);
}
