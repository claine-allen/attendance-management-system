package com.example.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.main.model.Department;

import java.util.Optional;

/**
 * Repository interface for Department entities.
 * Provides standard CRUD operations for Department data.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department,Long>{
    /**
     * Finds a Department by its name.
     * @param name The name of the department.
     * @return An Optional containing the Department if found, or empty.
     */
    Optional<Department> findByName(String name);

    /**
     * Finds a Department by its code.
     * @param code The code of the department.
     * @return An Optional containing the Department if found, or empty.
     */
    Optional<Department> findByCode(String code);

    /**
     * Checks if a department exists with the given name.
     * @param name The name to check.
     * @return True if a department with the name exists, false otherwise.
     */
    boolean existsByName(String name);

    /**
     * Checks if a department exists with the given code.
     * @param code The code to check.
     * @return True if a department with the code exists, false otherwise.
     */
    boolean existsByCode(String code);
}
