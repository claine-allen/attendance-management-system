package com.example.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.main.model.Department;
import com.example.main.model.Subject;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Subject entities.
 * Provides standard CRUD operations and custom query methods for Subject data.
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject,Long>{

    /**
     * Finds a Subject by its code.
     * @param code The code of the subject.
     * @return An Optional containing the Subject if found, or empty.
     */
    Optional<Subject> findByCode(String code);

    /**
     * Finds all Subjects belonging to a specific Department.
     * @param department The Department entity.
     * @return A list of Subjects associated with the given department.
     */
    List<Subject> findByDepartment(Department department);

    /**
     * Checks if a subject exists with the given code.
     * @param code The code to check.
     * @return True if a subject with the code exists, false otherwise.
     */
    boolean existsByCode(String code);
}
