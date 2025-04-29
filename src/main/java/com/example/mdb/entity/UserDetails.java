package com.example.mdb.entity;

import com.example.mdb.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Maps superclass to base table; subclasses in their own tables joined via PK
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class UserDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID) // Auto-generate a unique UUID
        private String userId;

        private String username;

        @Column(unique = true)
        private String email;

        private String password;
        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        private UserRole userRole;

        private LocalDate dateOfBirth;

        @CreatedDate
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime createdAt;

        @LastModifiedDate
        private LocalDateTime updatedAt;

        // Soft deletion fields (internal only)
        @JsonIgnore
        @Column(name = "is_deleted", nullable = false)
        private boolean isDeleted = false;

        @JsonIgnore
        @Column(name = "deleted_at")
        private Instant deletedAt;
}
