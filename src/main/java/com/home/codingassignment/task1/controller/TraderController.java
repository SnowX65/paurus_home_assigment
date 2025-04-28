package com.home.codingassignment.task1.controller;

import com.home.codingassignment.task1.entity.ResponseHandler;
import com.home.codingassignment.task1.model.TraderDto;
import com.home.codingassignment.task1.repository.TraderRepository;
import com.home.codingassignment.task1.entity.Country;
import com.home.codingassignment.task1.model.BetEstimateRequestDto;
import com.home.codingassignment.task1.model.BetEstimateResponseDto;
import com.home.codingassignment.task1.entity.Trader;
import com.home.codingassignment.task1.repository.CountryRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequestMapping("api/traders")
public class TraderController {

    private final TraderRepository traderRepository;
    private final CountryRepository countryRepository;

    public TraderController(TraderRepository traderRepository, CountryRepository countryRepository) {
        this.traderRepository = traderRepository;
        this.countryRepository = countryRepository;
    }

    /**
     * Returns a list of all traders
     */
    @GetMapping
    public ResponseEntity<Object> getTraders() {

        return new ResponseHandler()
                .setData(traderRepository.findAll())
                .generateResponse();
    }

    /**
     * Returns a single trader with the specified id
     * @param id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getTrader(@PathVariable Long id) {

        Trader trader = traderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Trader with id " + id + " not found."));

        return new ResponseHandler()
                .setData(trader)
                .generateResponse();
    }

    /**
     * Adds a new trader
     *
     * @param traderDto Trader data
     * @return A success response with the newly created trader
     */
    @PostMapping("/createTrader")
    public ResponseEntity<Object> createTrader(@RequestBody @Validated(TraderDto.OnCreate.class) TraderDto traderDto) {

        // checks if the country exists
        Country country = countryRepository.findById(traderDto.getCountryId())
                .orElseThrow(() -> new NoSuchElementException("Country with id " + traderDto.getCountryId() + " not found."));

        // check if a trader with that name already exists in this country
        if(traderRepository.findByNameAndCountry(traderDto.getName(), country.getId()).isPresent()) {
            throw new DataIntegrityViolationException("Trader with name " + traderDto.getName() + " already exists in country with id: " + traderDto.getCountryId());
        }

        Trader trader = new Trader()
                .setName(traderDto.getName())
                .setCountry(country);

        Trader savedTrader = traderRepository.save(trader);

        return new ResponseHandler()
                .setMessage("Trader created successfully")
                .setData(savedTrader)
                .generateResponse();
    }

    /**
     * Updates an existing trader
     *
     * @param id The ID of the trader to update
     * @param traderDto The fields to update
     * @return A success response with the updated trader
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTrader(@PathVariable Long id, @RequestBody @Validated(TraderDto.OnUpdate.class) TraderDto traderDto) {

        ResponseHandler responseHandler = new ResponseHandler();

        Trader trader = traderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Trader with id " + id + " not found."));

        Country country = new Country();

        if (traderDto.getCountryId() != null && !Objects.equals(trader.getCountry().getId(), traderDto.getCountryId())) {
            country = countryRepository.findById(traderDto.getCountryId())
                    .orElseThrow(() -> new NoSuchElementException("Country with id " + traderDto.getCountryId() + " not found."));
            trader.setCountry(country);
        }

        if (traderDto.getName() != null && !Objects.equals(traderDto.getName(), trader.getName())) {

            // check if a trader with that name already exists in this country
            if(traderRepository.findByNameAndCountry(traderDto.getName(), country.getId()).isPresent()) {
                throw new DataIntegrityViolationException("Trader with name " + traderDto.getName() + " already exists in country with id: " + traderDto.getCountryId());
            }

            trader.setName(traderDto.getName());
        }


        Trader updatedTrader = traderRepository.save(trader);

        return responseHandler
                .setMessage("Trader updated successfully")
                .setData(updatedTrader)
                .generateResponse();
    }


    /**
     * Deletes a trader by its ID.
     *
     * @param id The ID of the trader to delete.
     * @return A success response if deletion is successful.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTrader(@PathVariable Long id) {
        Trader trader = traderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Trader with id " + id + " not found."));

        traderRepository.delete(trader);

        return new ResponseHandler()
                .setMessage("Trader deleted successfully")
                .generateResponse();
    }


    /**
     * Receives a request to calculate the best possible return amount of a bet by a specific trader
     *
     * Taxation is calculated with one of the two types that the country the trader is in supports:
     *      - General ('G') - taxes the entire bet (played amount + winnings)
     *      - Winnings ('W') - taxes only the winnings
     *
     * Each country only supports one type of taxation method
     *
     * Each tax method has two types of taxation that apply to the amount taxed by each method. Each method lowers the
     * possible return amount.
     *      - Rate (possible return amount = possible return amount - (amount taxed * rate))
     *      - Amount - (possible return amount = possible return amount - amount)
     *
     * Trader always chooses the type of taxation that returns the biggest possible return amount to the user
     * Country can support both types of taxation or just one!
     *
     * Depending on the method the trader chooses, either the taxRate or taxAmount will be sent to the user
     *
     * @param BetEstimateDto
     */
    @PostMapping("/calculatePossibleBetReturnAmount")
    public ResponseEntity<Object> returnPossibleReturnAmount(@RequestBody @Valid BetEstimateRequestDto betEstimateDto) {

        // checks if the trader exists
        Trader trader = traderRepository.findById(betEstimateDto.getTraderId())
                .orElseThrow(() -> new NoSuchElementException("Trader with id " + betEstimateDto.getTraderId().toString() + " not found"));

        //calculate the best possible return amount before and after tax
        return getBestPossibleReturnAmountAfterTax(trader, betEstimateDto.getPlayedAmount(), betEstimateDto.getOdd());

    }

    private ResponseEntity<Object> getBestPossibleReturnAmountAfterTax(Trader trader, Double playedAmount, Double odd){

        //initialize the return object
        BetEstimateResponseDto betEstimateResponseDto = new BetEstimateResponseDto();

        double amountToBeTaxed = 0; // amount that needs to be taxed
        double possibleReturnAmountAfterTax = 0;
        double possibleReturnAmountBefTax = playedAmount*odd;
        double winnings = possibleReturnAmountBefTax - playedAmount;
        String taxationMethod = ""; // which taxation type was chosen by the trader - rate or amount

        double taxationAmount = 0.0; // amount of tax needed to be paid

        //Depending on the taxation type we set the amount to be taxed
        if(trader.getCountry().getTaxType() == 'G'){

            amountToBeTaxed = possibleReturnAmountBefTax;

        } else if (trader.getCountry().getTaxType() == 'W'){

            amountToBeTaxed = winnings;
        }

        Double totalTax_rateMethod = null;
        Double totalTax_amountMethod = null;
        
        // first we need to select the best taxation type depending on what the country supports
        // if it supports both types we select the type that brings the player the biggest possible return amount after tax
        if (trader.getCountry().getTaxRate() != null && trader.getCountry().getTaxAmount() == null) {

            taxationMethod = "rate";
            taxationAmount = amountToBeTaxed * trader.getCountry().getTaxRate();

        }  else if (trader.getCountry().getTaxAmount() != null && trader.getCountry().getTaxRate() == null) {

            taxationMethod = "amount";
            taxationAmount = trader.getCountry().getTaxAmount();

        } else {

            totalTax_rateMethod = amountToBeTaxed * trader.getCountry().getTaxRate();
            totalTax_amountMethod = trader.getCountry().getTaxAmount();

            if (totalTax_amountMethod < totalTax_rateMethod) {

                taxationMethod = "amount";
                taxationAmount = totalTax_amountMethod;

            } else {

                taxationMethod = "rate";
                taxationAmount = totalTax_rateMethod;

            }

        }

        //
        if (trader.getCountry().getTaxType() == 'W' && taxationAmount > winnings) {
            return betIsTooSmall(winnings, taxationAmount, trader.getCountry().getTaxType());

        } else if(trader.getCountry().getTaxType() == 'G' && taxationAmount > possibleReturnAmountBefTax) {
            return betIsTooSmall(possibleReturnAmountBefTax, taxationAmount, trader.getCountry().getTaxType());
        }

        possibleReturnAmountAfterTax = possibleReturnAmountBefTax - taxationAmount;


        betEstimateResponseDto.setTaxRate(trader.getCountry().getTaxRate());
        betEstimateResponseDto.setTaxAmount(trader.getCountry().getTaxAmount());
        betEstimateResponseDto.setTaxMethod(taxationMethod);
        betEstimateResponseDto.setPossibleReturnAmountBefTax(possibleReturnAmountBefTax);
        betEstimateResponseDto.setPossibleReturnAmountAfterTax(possibleReturnAmountAfterTax);

        return new ResponseHandler()
                .setData(betEstimateResponseDto)
                .generateResponse();

    }

    private ResponseEntity<Object> betIsTooSmall(Double returnAmount, Double taxAmount, Character taxType){

        String message = "";

        Map<String, Double> map = new HashMap<String, Double>();

        if (taxType == 'W'){
            message = "Tax amount exceeds the winnings";
            map.put("Winnings", returnAmount);
            map.put("TotalTax", taxAmount);

        } else {
            message = "Tax amount exceeds the possible return";
            map.put("ReturnAmount", returnAmount);
            map.put("TotalTax", taxAmount);
        }

        return new ResponseHandler()
            .setSuccessful(false)
            .setStatus(HttpStatus.BAD_REQUEST)
            .setMessage(message)
            .setData(map)
            .generateResponse();

    }

}
