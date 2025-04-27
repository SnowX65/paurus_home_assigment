package com.home.codingassignment.task1.repository;

import com.home.codingassignment.task1.entity.AllowedCountryNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllowedCountryNamesRepository extends JpaRepository<AllowedCountryNames, Long> {

    // Automatically performs case-insensitive search
    Optional<AllowedCountryNames> findByNameIgnoreCase(String countryName);
}
