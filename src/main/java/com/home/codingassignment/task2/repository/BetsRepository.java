package com.home.codingassignment.task2.repository;

import com.home.codingassignment.task2.entity.Bets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BetsRepository extends JpaRepository<Bets, Long>{
}
