package com.example.mdb.repository;

import com.example.mdb.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, String > {

    List<Screen> findByTheater_TheaterId(String theaterId);
}
