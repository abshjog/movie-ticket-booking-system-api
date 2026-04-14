package com.example.mdb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "theaters", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "address", "city"})
})
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "theater_id")
    private String theaterId;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "landmark")
    private String landmark;

    @OneToMany(mappedBy = "theater")
    @JsonIgnore
    private List<Screen> screens;

    @OneToMany(mappedBy = "theater")
    @JsonIgnore
    private List<Show> shows;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @ManyToOne
    @JoinColumn(name = "theater_owner_id")
    @JsonIgnore
    private TheaterOwner theaterOwner;
}
