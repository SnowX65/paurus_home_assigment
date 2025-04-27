package com.home.codingassignment.task1.repository;

import com.home.codingassignment.task1.entity.Trader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraderRepository extends JpaRepository<Trader, Long> {

    @Query(value = "SELECT * FROM trader t WHERE t.name = :name AND t.country_id = :countryId", nativeQuery = true)
    Optional<Trader> findByNameAndCountry(@Param("name") String name, @Param("countryId") Long countryId);
}
