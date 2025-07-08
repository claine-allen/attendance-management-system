package com.example.main.model;

import java.time.LocalDate; // For lecture date
import java.time.LocalTime; // For lecture start/end times
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a scheduled lecture or class.
 * This entity maps to the 'lectures' table.
 */
@Entity
@Table(name = "lectures") // Maps to the 'lectures' table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the lecture

    // Many-to-One relationship: Many Lectures are for one Subject
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching for performance
    @JoinColumn(name = "subject_id", nullable = false) // Foreign key column in 'lectures' table
    private Subject subject; // The subject taught in this lecture

    // Many-to-One relationship: Many Lectures are conducted by one Teacher
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching for performance
    @JoinColumn(name = "teacher_id", nullable = false) // Foreign key column in 'lectures' table
    private Teacher teacher; // The teacher conducting this lecture

    @Column(name = "lecture_date", nullable = false) // Date of the lecture
    private LocalDate lectureDate;

    @Column(name = "start_time", nullable = false) // Start time of the lecture
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false) // End time of the lecture
    private LocalTime endTime;

    @Column(name = "student_group", nullable = false, length = 100) // Describes the target student group
    private String studentGroup; // E.g., "B.Tech CSE 3rd Sem A"

    @Column(name = "room_number", length = 50) // Optional room number
    private String roomNumber; // Optional: Lecture room number

    // Bidirectional relationship: One Lecture can have many Attendance Records
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevent infinite recursion in JSON serialization
    private List<AttendanceRecord> attendanceRecords;
}
