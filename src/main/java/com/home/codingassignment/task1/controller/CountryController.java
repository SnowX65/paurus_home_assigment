package com.home.codingassignment.task1.controller;

import com.home.codingassignment.task1.model.CountryDto;
import com.home.codingassignment.task1.repository.CountryRepository;
import com.home.codingassignment.task1.entity.ResponseHandler;
import com.home.codingassignment.task1.entity.AllowedCountryNames;
import com.home.codingassignment.task1.entity.Country;
import com.home.codingassignment.task1.repository.AllowedCountryNamesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequestMapping("api/countries")
public class CountryController {

    private final CountryRepository countryRepository;
    private final AllowedCountryNamesRepository allowedCountryNamesRepository;

    public CountryController(com.home.codingassignment.task1.repository.CountryRepository countryRepository,
                             AllowedCountryNamesRepository allowedCountryNamesRepository) {
        this.countryRepository = countryRepository;
        this.allowedCountryNamesRepository = allowedCountryNamesRepository;
    }

    /**
     * Returns all countries
     */
    @GetMapping("")
    public ResponseEntity<Object> getCountries() {

        return new ResponseHandler()
                    .setData(countryRepository.findAll())
                    .generateResponse();
    }

    /**
     * Returns a single country with the specified id
     * @param id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getTrader(@PathVariable Long id) {

        return new ResponseHandler()
                    .setData(countryRepository.findById(id))
                    .generateResponse();
    }

    /**
     * Returns all allowed country names
     */
    @GetMapping("/allowedCountryNames")
    public ResponseEntity<Object> getAllowedCountryNames() {

        return new ResponseHandler()
                    .setData(allowedCountryNamesRepository.findAll())
                    .generateResponse();
    }


    /**
     * Creates a country and returns the newly created countries id
     *
     * @param countryDto
     */
    @PostMapping("/createCountry")
    public ResponseEntity<Object> returnPossibleReturnAmount(@RequestBody @Validated(CountryDto.OnCreate.class) CountryDto countryDto) {

        // checks that at least one of taxRate or taxAmount have a value
        if(countryDto.getTaxRate() == null && countryDto.getTaxAmount() == null) {
            return new ResponseHandler()
                    .setSuccessful(false)
                    .setMessage("Country needs to either have a set tax rate or amount or both.")
                    .generateResponse();
        }

        // checks if the requested country exists
        AllowedCountryNames allowedCountryNames = allowedCountryNamesRepository.findByNameIgnoreCase(countryDto.getName())
                .orElseThrow(() -> new NoSuchElementException("Wrong country name! Check /api/countries/allowedCountryNames for a list of allowed names"));

        Country country = new Country().setName(allowedCountryNames.getName())
            .setTaxRate(countryDto.getTaxRate())
            .setTaxAmount(countryDto.getTaxAmount())
            .setTaxType(countryDto.getTaxType());

        Country savedCountry = countryRepository.save(country);

        return new ResponseHandler()
                .setMessage("Country created successfully")
                .setData(savedCountry)
                .generateResponse();

    }


    /**
     * Updates an existing country with the given ID.
     * Only fields provided in the request will be updated
     *
     * Validation rules:
     *  - Either taxRate or taxAmount must be provided (one of them can be null).
     *  - TaxType must be either 'W' (winnings) or 'G' (general), if provided.
     *
     * @param id The ID of the country to update.
     * @param dto The request body containing fields to update.
     * @return A success response containing the updated country information.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCountry(@PathVariable Long id, @RequestBody @Validated(CountryDto.OnUpdate.class) CountryDto countryDto) {

        ResponseHandler responseHandler = new ResponseHandler();

        // checks if the requested country exists
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Country with id " + id + " not found."));

        if (countryDto.getName() != null) {
            responseHandler.setWarnings("The 'name' field was provided but not updated.");
        }

        if (countryDto.taxRateIsIncluded() && !Objects.equals(countryDto.getTaxRate(), country.getTaxRate())) {
            country.setTaxRate(countryDto.getTaxRate());
        }
        if (countryDto.taxAmountIsIncluded() && !Objects.equals(countryDto.getTaxAmount(), country.getTaxAmount())) {
            country.setTaxAmount(countryDto.getTaxAmount());
        }
        if (!countryDto.taxTypeIsEmpty() && !Objects.equals(countryDto.getTaxType(), country.getTaxType())) {
            country.setTaxType(countryDto.getTaxType());
        }

        // validates that one of tax_rate or tax_amount is present
        if (country.getTaxRate() == null && country.getTaxAmount() == null &&
            countryDto.taxRateIsIncluded() && countryDto.taxAmountIsIncluded()) {

            return new ResponseHandler()
                        .setStatus(HttpStatus.CONFLICT)
                        .setMessage("Either tax_rate or tax_amount must be provided.")
                        .setSuccessful(false)
                        .generateResponse();
        }


        // saves the country and sends a successful response
        countryRepository.save(country);

        return responseHandler
                .setMessage("Country updated successfully")
                .setData(country)
                .generateResponse();
    }


    /**
     * Deletes a country with the provided ID
     *
     * @param id The ID of the country to delete.
     * @return A success response if deletion is successful.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCountry(@PathVariable Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Country with id " + id + " not found."));

        countryRepository.delete(country);

        return new ResponseHandler()
                .setMessage("Country deleted successfully")
                .generateResponse();
    }

}
