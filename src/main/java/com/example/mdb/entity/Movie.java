package com.example.mdb.entity;

import com.example.mdb.enums.Certificate;
import com.example.mdb.enums.Genre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "movie_id")
    private String movieId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String Description;

    // private String[] Cast;
    @ElementCollection
    private Set<String> castList;

    @Column(name = "runtime")
    private Duration runtime;

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate")
    private Certificate certificate;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private Genre genre;
}
