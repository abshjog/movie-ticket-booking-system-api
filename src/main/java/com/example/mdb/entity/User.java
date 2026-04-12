package com.example.mdb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class User extends UserDetails{

    @OneToMany(mappedBy = "user")
    private List<Feedback> feedbacks;
}
