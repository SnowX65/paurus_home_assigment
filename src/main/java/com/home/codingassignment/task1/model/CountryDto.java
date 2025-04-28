package com.home.codingassignment.task1.model;

import jakarta.validation.constraints.*;

public class CountryDto {

    public interface OnCreate {}
    public interface OnUpdate {}

    @NotBlank(message = "Name is required", groups = OnCreate.class)
    private String name;

    @DecimalMin(value = "0.00", inclusive = true, groups = { OnCreate.class, OnUpdate.class })
    @DecimalMax(value = "0.99", inclusive = true, groups = { OnCreate.class, OnUpdate.class })
    private Double taxRate;
    private Boolean taxRateIsIncluded = false; //flag to check if the field is set in post request (required for updating, since the field can be set to null)

    @DecimalMin(value = "0.00", inclusive = true, groups = { OnCreate.class, OnUpdate.class })
    @DecimalMax(value = "1000.00", inclusive = true, groups = { OnCreate.class, OnUpdate.class })
    private Double taxAmount;
    private Boolean taxAmountIsIncluded = false; //flag to check if the field is set in post request (required for updating, since the field can be set to null)

    @NotBlank(groups = OnCreate.class)
    @Pattern(regexp = "[WG]", message = "must be W (winnings) or G (general)", groups = { OnCreate.class, OnUpdate.class })
    private String taxType;

    public CountryDto() {
        this.taxRateIsIncluded = false;
        this.taxAmountIsIncluded = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
        this.taxRateIsIncluded = true;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
        this.taxAmountIsIncluded = true;
    }

    public Character getTaxType() {
        if(this.taxType == null) {
            return Character.MIN_VALUE;
        }
        return this.taxType.charAt(0);
    }

    public void setTaxType(String taxType) {

        this.taxType = taxType;
    }

    public Boolean taxRateIsIncluded() {
        return taxRateIsIncluded;
    }

    public Boolean taxAmountIsIncluded() {
        return taxAmountIsIncluded;
    }

    public Boolean taxTypeIsEmpty(){
        return (this.taxType == null || this.taxType.isEmpty());
    }
}