package com.home.codingassignment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.home.codingassignment.task1.entity.AllowedCountryNames;
import com.home.codingassignment.task1.entity.Country;
import com.home.codingassignment.task1.entity.Trader;
import com.home.codingassignment.task1.repository.AllowedCountryNamesRepository;
import com.home.codingassignment.task1.repository.CountryRepository;
import com.home.codingassignment.task1.repository.TraderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class CodingAssignmentApplicationTests {


    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private AllowedCountryNamesRepository allowedCountryNamesRepository;

    @Autowired
    private TraderRepository traderRepository;

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setup() {


        allowedCountryNamesRepository.save(
             new AllowedCountryNames("Poland")
        );

        allowedCountryNamesRepository.save(
                new AllowedCountryNames("Hungary")
        );

        allowedCountryNamesRepository.save(
                new AllowedCountryNames("Germany")
        );

        allowedCountryNamesRepository.save(
                new AllowedCountryNames("Slovenia")
        );

        Country countrySlovenia = new Country()
                .setName("Slovenia")
                .setTaxRate(0.10)
                .setTaxAmount(2.00)
                .setTaxType('G');

        Country countryGermany = new Country()
                .setName("Germany")
                .setTaxRate(0.10)
                .setTaxAmount(2.00)
                .setTaxType('W');

        countryRepository.save(countrySlovenia);
        countryRepository.save(countryGermany);

        traderRepository.save(new Trader()
                .setName("Trader Slovenia")
                .setCountry(countrySlovenia));

        traderRepository.save(new Trader()
                .setName("Trader Germany")
                .setCountry(countryGermany));

    }

    // Tests for country api endpoints
    /**
     * Tests get for allowed country names
     * */
    @Test
    void testGetAllowedCountryNames() throws Exception {

        ResultActions result = mockMvc.perform(get("/api/countries/allowedCountryNames"));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").isNotEmpty());


    }



    /**
     * Tests get for all countries
     * */
    @Test
    void testGetCountries() throws Exception {

        ResultActions result = mockMvc.perform(get("/api/countries"));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Slovenia"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Germany"));

    }

    /**
     * Tests get for countries
     */
    @Test
    void testGetCountry() throws Exception {

        ResultActions result = mockMvc.perform(get("/api/countries/1"));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Slovenia"));

    }

    /**
     * Tests update for countries
     */
    @Test
    void testUpdateCountry() throws Exception {

        // updates a country successfully

        ObjectNode countryJson = new ObjectMapper().createObjectNode()
                .put("name", "Hungary")
                .put("taxRate", 0.15)
                .put("taxType", "W");

        ResultActions result = mockMvc.perform(
                put("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(countryJson.toString()));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Country updated successfully"))
                .andExpect(jsonPath("$.warnings").value("The 'name' field was provided but not updated."))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.data.name").value("Slovenia"))
                .andExpect(jsonPath("$.data.taxType").value("W"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.taxRate").value(0.15));

        //Unsuccessful update
        countryJson = new ObjectMapper().createObjectNode()
                .put("taxRate", 1.15)
                .put("taxAmount", 2000.15)
                .put("taxType", 'X');


        result = mockMvc.perform(
                put("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(countryJson.toString()));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Parameter validation failed"))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.data.taxRate").value("must be less than or equal to 0.99"))
                .andExpect(jsonPath("$.data.taxAmount").value("must be less than or equal to 1000.00"))
                .andExpect(jsonPath("$.data.taxType").value("must be W (winnings) or G (general)"));

    }

    /**
     * Tests creation of countries
     */
    @Test
    void testCreateCountry() throws Exception {

        // successfully creates a country

        ObjectNode countryJson = new ObjectMapper().createObjectNode()
                .put("name", "Poland")
                .put("taxRate", 0.15)
                .put("taxType", "W");

        ResultActions result = mockMvc.perform(
                post("/api/countries/createCountry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(countryJson.toString()));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Country created successfully"))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.data.name").value("Poland"))
                .andExpect(jsonPath("$.data.taxType").value("W"))
                .andExpect(jsonPath("$.data.taxAmount").isEmpty())
                .andExpect(jsonPath("$.data.taxRate").value(0.15));


        // bad name

        countryJson = new ObjectMapper().createObjectNode()
                .put("name", "PolandX")
                .put("taxRate", 0.15)
                .put("taxType", "W");

        result = mockMvc.perform(
                post("/api/countries/createCountry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(countryJson.toString()));

        result.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.error").value("Wrong country name! Check /api/countries/allowedCountryNames for a list of allowed names"));


        // bad parameters (tax amount, rate and type)
        countryJson = new ObjectMapper().createObjectNode()
                .put("name", "Poland")
                .put("taxAmount", -1)
                .put("taxRate", 1.15)
                .put("taxType", "L");

        result = mockMvc.perform(
                post("/api/countries/createCountry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(countryJson.toString()));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.data.taxType").value("must be W (winnings) or G (general)"))
                .andExpect(jsonPath("$.data.taxAmount").value("must be greater than or equal to 0.00"))
                .andExpect(jsonPath("$.data.taxRate").value("must be less than or equal to 0.99"))
                .andExpect(jsonPath("$.error").value("Parameter validation failed"));

        // bad parameters - one or both of tax amount or rate need to be set
        countryJson = new ObjectMapper().createObjectNode()
                .put("name", "Slovakia")
                .put("taxType", "W");

        result = mockMvc.perform(
                post("/api/countries/createCountry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(countryJson.toString()));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.error").value("Country needs to either have a set tax rate or amount or both."));

    }

    /**
     * Tests deletion of countries
     */
    @Test
    void testDeleteCountry() throws Exception {

        // successfully deletes a country
        Country country = countryRepository.save(new Country()
                .setName("Poland")
                .setTaxRate(0.10)
                .setTaxAmount(2.00)
                .setTaxType('W'));

        ResultActions result = mockMvc.perform(delete("/api/countries/" + country.getId().toString()));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Country deleted successfully"))
                .andExpect(jsonPath("$.successful").value(true));


        // cannot delete the country because of fk constraints
        country = countryRepository.save(new Country()
                .setName("Poland")
                .setTaxRate(0.10)
                .setTaxAmount(2.00)
                .setTaxType('W'));


        traderRepository.save(new Trader()
                .setCountry(country)
                .setName("Trader Poland"));

        result = mockMvc.perform(delete("/api/countries/" + country.getId().toString()));

        result.andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid reference to another entity (foreign key violation)"))
                .andExpect(jsonPath("$.successful").value(false));

        // cannot delete a non-existing country

        result = mockMvc.perform(delete("/api/countries/100"));

        result.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Country with id 100 not found."))
                .andExpect(jsonPath("$.successful").value(false));

    }



    // Tests for traders api endpoints

    /**
     * Tests get for all traders
     * */
    @Test
    public void getTraders() throws Exception {

        ResultActions result = mockMvc.perform(get("/api/traders"));

        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successful").value(true))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].name").value("Trader Slovenia"))
            .andExpect(jsonPath("$.data[0].country.id").value(1))
            .andExpect(jsonPath("$.data[1].id").value(2))
            .andExpect(jsonPath("$.data[1].name").value("Trader Germany"))
            .andExpect(jsonPath("$.data[1].country.id").value(2));

    }

    /**
     * Tests get for a single trader with specified id
     */
    @Test
    public void getTraderById() throws Exception {

        // return mockMvc.perform(get("/api/traders/1")));



        // trader exists
        ResultActions result = mockMvc.perform(get("/api/traders/1"));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Trader Slovenia"))
                .andExpect(jsonPath("$.data.country.id").value(1));

        // trader doesn't exist
        result = mockMvc.perform(get("/api/traders/3"));

        result.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.error").value("Trader with id 3 not found."));

        // bad parameter value
        result = mockMvc.perform(get("/api/traders/a"));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.error").value("Invalid format for path variable: id"));

    }

    /**
    * Tests creating a new trader
     */
    @Test
    public void testCreateTrader() throws Exception {

        // create a trader successfully

        ObjectNode traderJson = new ObjectMapper().createObjectNode()
                .put("name", "Trader 1")
                .put("countryId", 1);

        ResultActions result = mockMvc.perform(
                post("/api/traders/createTrader")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(traderJson.toString()));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Trader created successfully"))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.data.name").value("Trader 1"))
                .andExpect(jsonPath("$.data.country.id").value(1));


        // successfully create a trader with the same name in the other country

        traderJson = new ObjectMapper().createObjectNode()
                .put("name", "Trader 1")
                .put("countryId", 2);

        result = mockMvc.perform(
                post("/api/traders/createTrader")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(traderJson.toString()));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Trader created successfully"))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.data.name").value("Trader 1"))
                .andExpect(jsonPath("$.data.country.id").value(2));


        // cannot create a trader with the same name in same country

        result = mockMvc.perform(
                post("/api/traders/createTrader")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(traderJson.toString()));

        result.andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Trader with name Trader 1 already exists in country with id: 2"))
                .andExpect(jsonPath("$.successful").value(false));
    }

    /**
     * Tests updating a trader
     */
    @Test
    public void testUpdateTrader() throws Exception {

        // update a trader successfully

        ObjectNode traderJson = new ObjectMapper().createObjectNode()
                .put("name", "Trader 1")
                .put("countryId", 1);

        ResultActions result = mockMvc.perform(
                put("/api/traders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(traderJson.toString()));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Trader updated successfully"))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.data.name").value("Trader 1"))
                .andExpect(jsonPath("$.data.country.id").value(1));


        // update a traders name

        traderJson = new ObjectMapper().createObjectNode()
                .put("countryId", 2);

         result = mockMvc.perform(
                put("/api/traders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(traderJson.toString()));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Trader updated successfully"))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.data.name").value("Trader 1"))
                .andExpect(jsonPath("$.data.country.id").value(2));


        // cannot update a trader with the same name another trader has in that country
        // creates a new country - Poland and trader with the name Trader Poland
        // Trader 1 cannot change its name to Trader Poland and country to Poland

        Country countryPoland = new Country()
                .setName("Poland")
                .setTaxRate(0.10)
                .setTaxAmount(2.00)
                .setTaxType('W');

        countryRepository.save(countryPoland);

        traderRepository.save(new Trader()
                .setName("Trader Poland")
                .setCountry(countryPoland));



        traderJson = new ObjectMapper().createObjectNode()
                .put("name", "Trader Poland")
                .put("countryId", countryPoland.getId());

        result = mockMvc.perform(
                put("/api/traders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(traderJson.toString()));

        result.andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Trader with name Trader Poland already exists in country with id: " + countryPoland.getId().toString()))
                .andExpect(jsonPath("$.successful").value(false));
    }

    /**
     * Tests deleting a trader
     */
    @Test
    void testDeleteTrader() throws Exception {

        // successfully deletes a trader
        Country country = countryRepository.save(new Country()
                .setName("Poland")
                .setTaxRate(0.10)
                .setTaxAmount(2.00)
                .setTaxType('W'));

        Trader trader = traderRepository.save(new Trader()
                .setName("Trader Poland")
                .setCountry(country));

        ResultActions result = mockMvc.perform(delete("/api/traders/" + trader.getId().toString()));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Trader deleted successfully"))
                .andExpect(jsonPath("$.successful").value(true));


        // cannot a trader if it doesn't exist
        result = mockMvc.perform(delete("/api/traders/100"));

        result.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Trader with id 100 not found."))
                .andExpect(jsonPath("$.successful").value(false));

        // bad format for path variable
        result = mockMvc.perform(delete("/api/traders/a"));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid format for path variable: id"))
                .andExpect(jsonPath("$.successful").value(false));
    }

    @Test
    void testTraderCalculatePossibleReturn() throws Exception {

        // create a new country and trader for the requests
        Country country = countryRepository.save(new Country().setName("Poland"));
        Trader trader = traderRepository.save(new Trader().setName("Trader Poland").setCountry(country));
        ResultActions result;

        // trader with only W - amount: 2
        country.setTaxAmount(2.00).setTaxType('W').setTaxRate(null);
        countryRepository.save(country);

        result = calculateBet(trader.getId(), 5.00, 1.5);
        checkBetCalculations(result, 7.50, 5.50, "amount");

        // trader with only W - rate: 0.5
        country.setTaxAmount(null).setTaxType('W').setTaxRate(0.5);
        countryRepository.save(country);

        result = calculateBet(trader.getId(), 5.00, 1.5);
        checkBetCalculations(result, 7.50, 6.25, "rate");


        // trader with only G - amount: 2
        country.setTaxAmount(2.00).setTaxType('G').setTaxRate(null);
        countryRepository.save(country);

        result = calculateBet(trader.getId(), 5.00, 1.5);
        checkBetCalculations(result, 7.50, 5.5, "amount");


        // trader with only G - rate: 0.25
        country.setTaxAmount(null).setTaxType('G').setTaxRate(0.25);
        countryRepository.save(country);

        result = calculateBet(trader.getId(), 5.00, 1.5);
        checkBetCalculations(result, 7.50, 5.63, "rate");

        // trader with W - amount and rate, amount is better
        country.setTaxAmount(1.00).setTaxType('W').setTaxRate(0.5);
        countryRepository.save(country);

        result = calculateBet(trader.getId(), 5.00, 1.5);
        checkBetCalculations(result, 7.50, 6.50, "amount");

        // trader with W - amount and rate, rate is better
        country.setTaxAmount(2.00).setTaxType('W').setTaxRate(0.5);
        countryRepository.save(country);

        result = calculateBet(trader.getId(), 5.00, 1.5);
        checkBetCalculations(result, 7.50, 6.25, "rate");

        // trader with G - amount and rate, amount is better
        country.setTaxAmount(1.00).setTaxType('G').setTaxRate(0.5);
        countryRepository.save(country);

        result = calculateBet(trader.getId(), 5.00, 1.5);
        checkBetCalculations(result, 7.50, 6.50, "amount");

        // trader with G - amount and rate, rate is better
        country.setTaxAmount(1.00).setTaxType('G').setTaxRate(0.1);
        countryRepository.save(country);

        result = calculateBet(trader.getId(), 5.00, 1.5);
        checkBetCalculations(result, 7.50, 6.75, "rate");

        // tax is too high - needs a bigger bet
        country.setTaxAmount(7.51).setTaxType('G').setTaxRate(null);
        countryRepository.save(country);

        result = calculateBet(trader.getId(), 5.00, 1.5);

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.error").value("Tax amount exceeds the possible return"))
                .andExpect(jsonPath("$.data.TotalTax").value(7.51))
                .andExpect(jsonPath("$.data.ReturnAmount").value(7.50));



        country.setTaxAmount(2.51).setTaxType('W').setTaxRate(null);
        countryRepository.save(country);

        result = calculateBet(trader.getId(), 5.00, 1.5);

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.error").value("Tax amount exceeds the winnings"))
                .andExpect(jsonPath("$.data.TotalTax").value(2.51))
                .andExpect(jsonPath("$.data.Winnings").value(2.50));


        // trader doesn't exist
        result = calculateBet(100L, 5.00, 1.5);

        result.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.error").value("Trader with id 100 not found"));

        // bad value for odd
        result = calculateBet(trader.getId(), 5.00, 0.99);

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.error").value("Parameter validation failed"))
                .andExpect(jsonPath("$.data.odd").value("must be greater than or equal to 1.01"));

        // bad value for playedAmount
        result = calculateBet(100L, 0.99, 1.5);

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.error").value("Parameter validation failed"))
                .andExpect(jsonPath("$.data.playedAmount").value("must be greater than or equal to 1.00"));

        // bad value for playedAmount and odd
        result = calculateBet(100L, -0.99, -0.99);

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.error").value("Parameter validation failed"))
                .andExpect(jsonPath("$.data.odd").value("must be greater than or equal to 1.01"))
                .andExpect(jsonPath("$.data.playedAmount").value("must be greater than or equal to 1.00"));

    }

    private ResultActions calculateBet(Long traderId, Double playedAmount, Double odd) throws Exception {

        ObjectNode requestJson = new ObjectMapper().createObjectNode()
                .put("traderId", traderId)
                .put("playedAmount", playedAmount)
                .put("odd", odd);

        return mockMvc.perform(
                post("/api/traders/calculatePossibleBetReturnAmount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson.toString()));

    }

    private void checkBetCalculations(ResultActions result, Double expectedValueBeforeTax,
         Double expectedValueAfterTax, String taxMethod) throws Exception {

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.data.possibleReturnAmountBefTax").value(expectedValueBeforeTax))
                .andExpect(jsonPath("$.data.possibleReturnAmountAfterTax").value(expectedValueAfterTax))
                .andExpect(jsonPath("$.data.taxMethod").value(taxMethod));
    }
}
