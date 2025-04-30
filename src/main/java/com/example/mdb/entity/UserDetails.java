package com.example.mdb.entity;

import com.example.mdb.enums.UserRole;
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
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name = "user_id")
        private String userId;

        @Column(name = "username")
        private String username;

        @Column(unique = true, name = "email")
        private String email;

        @Column(name = "password")
        private String password;

        @Column(name = "phone_number", length = 10)
        private String phoneNumber;

        @Enumerated(value = EnumType.STRING)
        @Column(name = "user_role")
        private UserRole userRole;

        @Column(name = "dob")
        private LocalDate dateOfBirth;

        @Column(name = "is_deleted")
        private boolean isDeleted;

        @Column(name = "deleted_at")
        private Instant deletedAt;

        @CreatedDate
        @Column(name = "created_at", nullable = false, updatable = false)
        private Instant createdAt;

        @LastModifiedDate
        @Column(name = "updated_at", nullable = false)
        private Instant updatedAt;
}
