package com.example.main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a student in the college.
 * This entity maps to the 'students' table and is linked to the 'users' table.
 */
@Entity
@Table(name = "students") // Maps to the 'students' table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the student

    // One-to-One relationship: Each Student is associated with one User account
    @OneToOne(fetch = FetchType.LAZY) // Lazy fetching for performance
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Foreign key to 'users' table, must be unique
    private User user; // The user account linked to this student

    @Column(name = "roll_number", nullable = false, unique = true, length = 50) // Roll number, unique, max 50 chars
    private String rollNumber; // Student's unique roll number

    // Many-to-One relationship: Many Students belong to one Department
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching for performance
    @JoinColumn(name = "department_id", nullable = false) // Foreign key column in 'students' table
    private Department department; // The department the student belongs to

    @Column(name = "batch_year", nullable = false) // Academic year student joined
    private Integer batchYear; // The academic year the student joined (e.g., 2022)

    @Column(length = 10) // Section, max 10 chars, can be null
    private String section; // Student's section (e.g., 'A', 'B'), optional

}
