package com.home.codingassignment.task2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Outcome {

    private String id;
    private ArrayList<String> specifiers = new ArrayList<>();

    public Outcome(String outcome_id, String specifiers) {

        this.id = outcome_id;
        addSpecifier(specifiers);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getSpecifiers() {
        return specifiers;
    }

    public void addSpecifier(String specifier) {
        specifiers.add(specifier);
    }

    /**
     * Sorts all the specifiers
     */
    public void sortSpecifiers() {
        specifiers.sort(Comparator.nullsFirst(String::compareTo));
    }

}
