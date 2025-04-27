package com.home.codingassignment.task1.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TraderDto {

    public interface OnCreate {}
    public interface OnUpdate {}

    @NotBlank(groups = OnCreate.class, message = "Trader name is required.")
    private String name;

    @NotNull(groups = OnCreate.class, message = "Country ID is required.")
    private Long countryId;

    // getters and setters

    public String getName() {
        return name;
    }

    public TraderDto setName(String name) {
        this.name = name.trim();
        return this;
    }

    public Long getCountryId() {
        return countryId;
    }

    public TraderDto setCountryId(Long countryId) {
        this.countryId = countryId;
        return this;
    }
}