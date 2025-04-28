package com.home.codingassignment.task1.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BetEstimateResponseDto {

    private Double possibleReturnAmountBefTax;
    private Double possibleReturnAmountAfterTax;
    private Double taxRate;
    private Double taxAmount;
    private String taxMethod;

    public BetEstimateResponseDto() {}

    public BetEstimateResponseDto(Double possibleReturnAmountBefTax, Double possibleReturnAmountAfterTax, Double taxRate, Double taxAmount) {
        this.possibleReturnAmountBefTax = possibleReturnAmountBefTax;
        this.possibleReturnAmountAfterTax = possibleReturnAmountAfterTax;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
    }

    public Double getPossibleReturnAmountBefTax() {
        return possibleReturnAmountBefTax;
    }

    public void setPossibleReturnAmountBefTax(Double possibleReturnAmountBefTax) {
        this.possibleReturnAmountBefTax = BigDecimal.valueOf(possibleReturnAmountBefTax).setScale(2, RoundingMode.HALF_UP).doubleValue();

    }

    public Double getPossibleReturnAmountAfterTax() {
        return possibleReturnAmountAfterTax;
    }

    public void setPossibleReturnAmountAfterTax(Double possibleReturnAmountAfterTax) {
        this.possibleReturnAmountAfterTax = BigDecimal.valueOf(possibleReturnAmountAfterTax).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTaxMethod() {
        return taxMethod;
    }

    public void setTaxMethod(String taxMethod) {
        this.taxMethod = taxMethod;
    }
}
