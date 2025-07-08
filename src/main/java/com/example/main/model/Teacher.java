package com.example.main.model;


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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Represents a teacher in the college.
 * This entity maps to the 'teachers' table and is linked to the 'users' table.
 */
@Entity
@Table(name = "teachers") // Maps to the 'teachers' table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the teacher

    // One-to-One relationship: Each Teacher is associated with one User account
    @OneToOne(fetch = FetchType.LAZY) // Lazy fetching for performance
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Foreign key to 'users' table, must be unique
    private User user; // The user account linked to this teacher

    @Column(name = "employee_id", nullable = false, unique = true, length = 50) // Employee ID, unique, max 50 chars
    private String employeeId; // College-assigned employee ID

    // Many-to-One relationship: Many Teachers belong to one Department
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching for performance
    @JoinColumn(name = "department_id", nullable = false) // Foreign key column in 'teachers' table
    private Department department; // The department the teacher belongs to

    // Bidirectional relationship: One Teacher can conduct many Lectures
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevent infinite recursion in JSON serialization
    private List<Lecture> lectures;
}
