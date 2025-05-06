package com.example.mdb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class User extends UserDetails{

    @OneToMany(mappedBy = "user")
    private List<Feedback> feedbacks;
}
