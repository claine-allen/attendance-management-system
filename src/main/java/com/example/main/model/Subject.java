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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an academic subject offered by a department.
 * This entity maps to the 'subjects' table.
 */
@Entity
@Table(name = "subjects") // Maps to the 'subjects' table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the subject

    @Column(nullable = false) // Subject name cannot be null
    private String name; // Full name of the subject

    @Column(nullable = false, unique = true, length = 50) // Subject code cannot be null, unique, max 50 chars
    private String code; // Short code for the subject (e.g., "CS301")

    // Many-to-One relationship: Many Subjects can belong to one Department
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching for performance
    @JoinColumn(name = "department_id", nullable = false) // Foreign key column in 'subjects' table
    private Department department; // The department offering this subject

    // Bidirectional relationship: One Subject can have many Lectures
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevent infinite recursion in JSON serialization
    private List<Lecture> lectures;
}
