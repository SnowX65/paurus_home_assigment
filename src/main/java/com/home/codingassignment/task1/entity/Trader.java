package com.home.codingassignment.task1.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trader")
public class Trader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "countryId")
    private com.home.codingassignment.task1.entity.Country country;

    public Trader() {

    }

    public Trader(String name, com.home.codingassignment.task1.entity.Country country) {
        this.name = name.trim();
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public Trader setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Trader setName(String name) {
        this.name = name.trim();
        return this;
    }

    public com.home.codingassignment.task1.entity.Country getCountry() {
        return country;
    }

    public Trader setCountry(com.home.codingassignment.task1.entity.Country country) {
        this.country = country;
        return this;
    }

}
