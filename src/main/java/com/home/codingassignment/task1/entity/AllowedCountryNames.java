package com.home.codingassignment.task1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ALLOWED_COUNTRY_NAMES")
public class AllowedCountryNames {

    @Id
    @Column(name = "name")
    private String name;

    public AllowedCountryNames() {

    }

    public AllowedCountryNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
