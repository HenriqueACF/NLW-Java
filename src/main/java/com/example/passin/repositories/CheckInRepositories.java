package com.example.passin.repositories;


import com.example.passin.domain.checkin.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepositories extends JpaRepository<CheckIn, Integer> {
}
