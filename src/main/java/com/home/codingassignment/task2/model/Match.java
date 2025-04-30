package com.home.codingassignment.task2.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Match {
    private String id;
    private Map<Long, Market> markets = new LinkedHashMap<>();

    public Match(String match_id, Long market_id, String outcome_id, String specifiers){

        this.id = match_id;
        addMarket(market_id, outcome_id, specifiers);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<Long, Market> getMarkets() {
        return markets;
    }

    public void setMarkets(Map<Long, Market> markets) {
        this.markets = markets;
    }

    /**
     * If a market exists add a new outcome to the market, otherwise create it.
     */
    public void addMarket(Long market_id, String outcome_id, String specifiers){

        Market market = markets.getOrDefault(market_id, null);

        if (market == null){
            markets.put(market_id, new Market(market_id, outcome_id, specifiers));
        } else {
            market.addOutcome(outcome_id, specifiers);
        }

    }

    /**
     * Sorts all the markets and its outcomes
     */
    public Match sort(){

        markets = new TreeMap<>(markets);

        markets.forEach((market_id,market)-> {
            market.sortOutcomes(); });

        return this;
    }

}