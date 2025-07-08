package com.example.main.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a department within the college.
 * This entity maps to the 'departments' table.
 */
@Entity
@Table(name = "departments") // Maps to the 'departments' table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the department

    @Column(nullable = false, unique = true) // Department name cannot be null and must be unique
    private String name; // Full name of the department

    @Column(nullable = false, unique = true, length = 20) // Department code cannot be null, must be unique, max 20 chars
    private String code; // Short code for the department (e.g., "CSE")

    // Bidirectional relationship: One Department can have many Subjects
    // @JsonIgnore helps prevent infinite recursion when converting to JSON
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevent infinite recursion in JSON serialization
    private List<Subject> subjects;

    // Bidirectional relationship: One Department can have many Teachers
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevent infinite recursion in JSON serialization
    private List<Teacher> teachers;

    // Bidirectional relationship: One Department can have many Students
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevent infinite recursion in JSON serialization
    private List<Student> students;
}
