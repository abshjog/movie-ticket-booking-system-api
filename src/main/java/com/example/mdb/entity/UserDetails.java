package com.example.mdb.entity;

import com.example.mdb.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Maps superclass to base table; subclasses in their own tables joined via PK
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Getter
@Setter
public class UserDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID) // Auto-generate a unique UUID
        private String userId;

        private String username;

        @Column(unique = true) // Ensures that each email value is unique across the table
        private String email;

        private String password;
        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        @Column(length = 20) // Max 20 chars: ensures enum values (e.g., THEATER_OWNER) fit without truncation
        private UserRole userRole;

        private LocalDate dateOfBirth;

        @CreationTimestamp
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        @UpdateTimestamp
        private LocalDateTime updatedAt;

        // Soft deletion fields (internal only)
        @JsonIgnore
        @Column(name = "is_deleted", nullable = false)
        private boolean isDeleted = false;

        @JsonIgnore
        @Column(name = "deleted_at")
        private Instant deletedAt;
}
