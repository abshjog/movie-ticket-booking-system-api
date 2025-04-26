package com.example.mdb.repository;

import com.example.mdb.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenRepository extends JpaRepository<Screen, String > {
}
