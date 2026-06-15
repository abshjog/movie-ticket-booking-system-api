package com.example.mdb.entity;

import com.example.mdb.enums.SeatCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "seat_id")
    private String seatId;

    @Column(name = "name")
    private String name;

    @Column(name = "row_label")
    private String rowLabel;

    @Column(name = "col_index")
    private Integer colIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_category")
    private SeatCategory seatCategory;

    @Column(name = "is_aisle")
    private boolean isAisle = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    @JsonIgnore
    private Screen screen;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;
}