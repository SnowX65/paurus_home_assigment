package com.home.codingassignment.task1.model;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BetEstimateRequestDto {

    @NotNull
    @Min(value = 0)
    private Long traderId;

    @NotNull
    @DecimalMin("1.00")
    private Double playedAmount;

    @NotNull
    @DecimalMin("1.01")
    private Double odd;

    public BetEstimateRequestDto() {
    }

    public Long getTraderId() {
        return traderId;
    }

    public void setTraderId(Long traderId) {
        this.traderId = traderId;
    }

    public Double getPlayedAmount() {
        return playedAmount;
    }

    public void setPlayedAmount(Double playedAmount) {
        this.playedAmount = BigDecimal.valueOf(playedAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();;
    }

    public Double getOdd() {
        return odd;
    }

    public void setOdd(Double odd) {
        this.odd = BigDecimal.valueOf(odd).setScale(2, RoundingMode.HALF_UP).doubleValue();;
    }
}
