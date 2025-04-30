package com.home.codingassignment.task2.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Market {

    private Long id;
    private Map<String, Outcome> outcomes = new LinkedHashMap<>();


    public Market(Long market_id, String outcome_id, String specifiers) {
        this.id = market_id;

        addOutcome(outcome_id, specifiers);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Outcome> getOutcomes() {
        return outcomes;
    }


    /**
     * If an outcome exists add the specifier to the outcome, otherwise create it.
     */
    public void addOutcome(String outcome_id, String specifiers) {

        Outcome outcome = outcomes.getOrDefault(outcome_id, null);

        if (outcome == null){
            outcomes.put(outcome_id, new Outcome(outcome_id, specifiers));
        } else {
            outcome.addSpecifier(specifiers);
        }

    }

    /**
     * Sorts all the outcomes and its specifiers
     */
    public void sortOutcomes(){

        outcomes = new TreeMap<>(outcomes);

        outcomes.forEach( (outcome_id, outcome) -> {outcome.sortSpecifiers();});

    }
}