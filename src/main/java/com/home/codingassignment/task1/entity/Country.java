package com.home.codingassignment.task1.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "country")
public class Country {

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "tax_rate")
    private Double taxRate;

    @Column(name = "tax_amount")
    private Double taxAmount;

    @Column(name = "tax_type")
    private Character taxType; // W = 'winnings', G = 'general'

    public Country() {

    }

    public Long getId() {
        return id;
    }

    public Country setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Country setName(String name) {
        this.name = name;
        return this;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public Country setTaxRate(Double tax_rate) {
        this.taxRate = tax_rate == null ? null : BigDecimal.valueOf(tax_rate).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return this;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public Country setTaxAmount(Double tax_amount) {
        this.taxAmount = tax_amount == null ? null : BigDecimal.valueOf(tax_amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return this;
    }

    public Character getTaxType() {
        return taxType;
    }

    public Country setTaxType(Character tax_type) {
        this.taxType = tax_type;
        return this;
    }

    public String toString() {
        return this.name;
    }

}
