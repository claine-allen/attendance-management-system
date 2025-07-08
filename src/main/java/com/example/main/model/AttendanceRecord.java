package com.example.main.model;

import java.time.LocalDateTime; // For marking timestamp

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an individual attendance record for a student in a specific lecture.
 * This entity maps to the 'attendance_records' table.
 */
@Entity
@Table(name = "attendance_records", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"lecture_id", "student_id"}) // Ensures only one record per student per lecture
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the attendance record

    // Many-to-One relationship: Many Attendance Records belong to one Lecture
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching for performance
    @JoinColumn(name = "lecture_id", nullable = false) // Foreign key column in 'attendance_records' table
    private Lecture lecture; // The lecture this record is for

    // Many-to-One relationship: Many Attendance Records are for one Student
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching for performance
    @JoinColumn(name = "student_id", nullable = false) // Foreign key column in 'attendance_records' table
    private Student student; // The student this record is for

    @Column(nullable = false, length = 20) // Status cannot be null, max 20 chars
    @Enumerated(EnumType.STRING) // Stores the enum name (e.g., "PRESENT", "ABSENT", "LEAVE") as a string
    private AttendanceStatus status; // Attendance status ('PRESENT', 'ABSENT', 'LEAVE')

    // Many-to-One relationship: Many Attendance Records are marked by one Teacher
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching for performance
    @JoinColumn(name = "marked_by_teacher_id", nullable = false) // Foreign key column in 'attendance_records' table
    private Teacher markedByTeacher; // The teacher who marked this attendance

    @Column(name = "marking_timestamp", nullable = false) // Timestamp of when the attendance was marked/updated
    private LocalDateTime markingTimestamp;
}
