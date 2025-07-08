package com.example.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.main.model.AttendanceRecord;
import com.example.main.model.Lecture;
import com.example.main.model.Student;

import java.time.LocalDate; // Import LocalDate
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AttendanceRecord entities.
 * Provides standard CRUD operations and custom query methods for Attendance Record data.
 */
@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    /**
     * Finds an AttendanceRecord for a specific Lecture and Student.
     * This is useful for checking if a record already exists before marking/updating.
     * @param lecture The Lecture entity.
     * @param student The Student entity.
     * @return An Optional containing the AttendanceRecord if found, or empty.
     */
    Optional<AttendanceRecord> findByLectureAndStudent(Lecture lecture, Student student);

    /**
     * Finds all AttendanceRecords for a specific Student.
     * @param student The Student entity.
     * @return A list of AttendanceRecords for the given student.
     */
    List<AttendanceRecord> findByStudent(Student student);

    /**
     * Finds all AttendanceRecords for a specific Lecture.
     * @param lecture The Lecture entity.
     * @return A list of AttendanceRecords for the given lecture.
     */
    List<AttendanceRecord> findByLecture(Lecture lecture);

    /**
     * Finds all AttendanceRecords for a specific Student within a given date range.
     * This helps in generating reports for a student over a period.
     * @param student The Student entity.
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return A list of AttendanceRecords for the student within the date range.
     */
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.student = :student AND ar.lecture.lectureDate BETWEEN :startDate AND :endDate")
    List<AttendanceRecord> findByStudentAndLectureDateBetween(
            @Param("student") Student student,
            @Param("startDate") LocalDate startDate, // Changed from LocalDateTime to LocalDate
            @Param("endDate") LocalDate endDate      // Changed from LocalDateTime to LocalDate
    );

    /**
     * Counts the number of times a student was present for lectures related to a specific subject.
     * This involves joining attendance records with lectures and subjects.
     * @param studentId The ID of the student.
     * @param subjectId The ID of the subject.
     * @return The count of 'PRESENT' records for the student in that subject.
     */
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar " +
            "WHERE ar.student.id = :studentId " +
            "AND ar.lecture.subject.id = :subjectId " +
            "AND ar.status = 'PRESENT'")
    long countPresentByStudentAndSubject(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    /**
     * Counts the total number of lectures for a specific subject that a student was expected to attend.
     * This considers all lectures associated with the subject within a given student group context (if applicable).
     * In a more complex system, this might involve checking student enrollments against lectures.
     * For simplicity, this query counts all lectures for a subject up to a certain date.
     * A more robust solution might require fetching all lectures for the student's *specific* group.
     * @param subjectId The ID of the subject.
     * @param studentGroup The student group identifier (e.g., "B.Tech CSE 3rd Sem A").
     * @param upToDate The maximum date for lectures to be considered.
     * @return The total count of lectures for that subject.
     */
    @Query("SELECT COUNT(l) FROM Lecture l " +
            "WHERE l.subject.id = :subjectId " +
            "AND l.studentGroup = :studentGroup " +
            "AND l.lectureDate <= :upToDate")
    long countTotalLecturesForSubjectAndStudentGroup(
            @Param("subjectId") Long subjectId,
            @Param("studentGroup") String studentGroup,
            @Param("upToDate") LocalDate upToDate
    );
}