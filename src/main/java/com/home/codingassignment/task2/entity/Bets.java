package com.home.codingassignment.task2.entity;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "bets")
public class Bets {

    private @Id
   // @GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
    //@SequenceGenerator(name = "generator", sequenceName = "bets_id_seq", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bets_seq")
    @SequenceGenerator(
            name = "bets_seq",
            sequenceName = "bets_id_seq",
            allocationSize = 1 // Must match DB sequence increment
    )
    @Column(name = "id")
    Long id;

    @Column(name = "insert_id")
    Long insertId;

    @Column(name = "match_id")
    private String matchId;

    @Column(name = "market_id")
    private Long marketId;

    @Column(name = "outcome_id")
    private String outcomeId;

    @Column(name = "specifiers")
    private String specifiers;

    public Bets() {

    }

    public Bets(String matchId, Long marketId, String outcomeId, String specifiers) {
        this.matchId = matchId;
        this.marketId = marketId;
        this.outcomeId = outcomeId;
        this.specifiers = specifiers;
    }

    public Bets(Long insertId, String matchId, Long marketId, String outcomeId, String specifiers) {
        this.insertId = insertId;
        this.matchId = matchId;
        this.marketId = marketId;
        this.outcomeId = outcomeId;
        this.specifiers = specifiers;
    }



    public String getSpecifiers() {
        return specifiers;
    }

    public void setSpecifiers(String specifiers) {
        this.specifiers = specifiers;
    }

    public String getOutcomeId() {
        return outcomeId;
    }

    public void setOutcomeId(String outcomeId) {
        this.outcomeId = outcomeId;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
}
