package com.example.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.main.model.Lecture;
import com.example.main.model.Subject;
import com.example.main.model.Teacher;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Lecture entities.
 * Provides standard CRUD operations and custom query methods for Lecture data.
 */
@Repository
public interface LectureRepository extends JpaRepository<Lecture,Long>{

    /**
     * Finds all Lectures conducted by a specific Teacher.
     * @param teacher The Teacher entity.
     * @return A list of Lectures conducted by the given teacher.
     */
    List<Lecture> findByTeacher(Teacher teacher);

    /**
     * Finds all Lectures for a specific Subject.
     * @param subject The Subject entity.
     * @return A list of Lectures for the given subject.
     */
    List<Lecture> findBySubject(Subject subject);

    /**
     * Finds all Lectures on a specific date.
     * @param lectureDate The date of the lectures.
     * @return A list of Lectures on the given date.
     */
    List<Lecture> findByLectureDate(LocalDate lectureDate);

    /**
     * Finds all Lectures for a specific student group.
     * @param studentGroup The student group identifier (e.g., "B.Tech CSE 3rd Sem A").
     * @return A list of Lectures for the specified student group.
     */
    List<Lecture> findByStudentGroup(String studentGroup);

    /**
     * Finds all Lectures conducted by a specific Teacher on a specific date.
     * This will be useful for teachers to see their daily schedule.
     * @param teacher The Teacher entity.
     * @param lectureDate The date of the lectures.
     * @return A list of Lectures conducted by the given teacher on the specified date.
     */
    List<Lecture> findByTeacherAndLectureDate(Teacher teacher, LocalDate lectureDate);

    /**
     * Finds all Lectures for a specific student group on a specific date.
     * This will be useful for students to see their daily schedule.
     * @param studentGroup The student group identifier.
     * @param lectureDate The date of the lectures.
     * @return A list of Lectures for the specified student group on the given date.
     */
    List<Lecture> findByStudentGroupAndLectureDate(String studentGroup, LocalDate lectureDate);

}
