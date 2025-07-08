package com.example.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.main.model.Department;
import com.example.main.model.Teacher;
import com.example.main.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Teacher entities.
 * Provides standard CRUD operations and custom query methods for Teacher data.
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher,Long>{

    /**
     * Finds a Teacher by their associated User.
     * @param user The User entity associated with the teacher.
     * @return An Optional containing the Teacher if found, or empty.
     */
    Optional<Teacher> findByUser(User user);

    /**
     * Finds a Teacher by their employee ID.
     * @param employeeId The employee ID of the teacher.
     * @return An Optional containing the Teacher if found, or empty.
     */
    Optional<Teacher> findByEmployeeId(String employeeId);

    /**
     * Finds all Teachers belonging to a specific Department.
     * @param department The Department entity.
     * @return A list of Teachers associated with the given department.
     */
    List<Teacher> findByDepartment(Department department);

    /**
     * Checks if a teacher exists with the given employee ID.
     * @param employeeId The employee ID to check.
     * @return True if a teacher with the employee ID exists, false otherwise.
     */
    boolean existsByEmployeeId(String employeeId);
}
