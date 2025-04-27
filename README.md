# Paurus home assigment

## Task 1 - REST API example

This project exposes RESTful APIs for managing Countries and Traders, and estimating possible bet return amounts after taxation.

### Run the app

### Run the tests



### Calculating the Best Possible Bet Return Amount

Taxation is calculated using one of the two types supported by the country in which the trader operates:

- **General ('G')**: Taxes the entire bet (played amount + winnings).
- **Winnings ('W')**: Taxes only the winnings
  
Each country only supports **one** type of taxation method.

#### Taxation Methods

Each tax method has two types of taxation that apply to the amount taxed by each method, which lowers the possible return amount:

- **Rate**: 
  - Formula: `possible return amount = possible return amount - (amount taxed * rate)`
- **Amount**: 
  - Formula: `possible return amount = possible return amount - amount`

### Trader's Choice

The trader always chooses the taxation type that provides the **best possible return amount**.

- A country can support **both types of taxation** or just **one**.

### Example

- **Amount bet** = 5  
- **Odd** = 1.5  
- **Return amount before tax** = 7.5

#### Country A (General Taxation)

- **Tax Type**: General (G)
- **Rate**: 10%
- **Amount**: 2

**Possible return amount after tax in Country A**:

- Amount to be taxed = 7.5
- With rate method: `7.5 - (7.5 * 0.1) = 6.75` (best possible return amount after tax)
- With amount method: `7.5 - 2 = 5.5`

#### Country B (Winnings Taxation)

- **Tax Type**: Winnings (W)
- **Rate**: 10%
- **Amount**: 2

**Possible return amount after tax in Country B**:

- Amount to be taxed = `7.5 - 5 = 2.5` 
- With rate method: `7.5 - (2.5 * 0.1) = 7.25` (best possible return amount after tax)
- With amount method: `7.5 - (2.5 - 2) = 5.5`

   
### API Endpoints

#### 1. Country API (/api/countries)

Method | Endpoint | Description
| ------------- | ------------- | ------------- |
GET | /api/traders | Fetch all traders
GET | /api/traders/{id} | Fetch a trader by ID
POST | /api/traders/createTrader | Create a new trader
PUT | /api/traders/{id} | Update an existing trader
DELETE | /api/traders/{id} | Delete a trader by ID
POST | /api/traders/calculatePossibleBetReturnAmount | Calculate the possible bet return amount after tax


Notes:

    Country creation only allows countries from a pre-approved list (AllowedCountryNames).

    Either taxRate or taxAmount (or both) must be provided when creating or updating a country.

    TaxType must be either:

        'W' for taxing winnings

        'G' for taxing the entire bet (general)

#### 2. Trader API (/api/traders)


Method | Endpoint | Description
| ------------- | ------------- | ------------- |
GET | /api/traders | Fetch all traders
GET | /api/traders/{id} | Fetch a trader by ID
POST | /api/traders/createTrader | Create a new trader
PUT | /api/traders/{id} | Update an existing trader
DELETE | /api/traders/{id} | Delete a trader by ID
POST | /api/traders/calculatePossibleBetReturnAmount | Calculate the possible bet return amount after tax

Notes:

    Trader creation requires selecting an existing country.

    Trader names can be reused across different countries (unless additional uniqueness is implemented).

    Bet Return Calculation uses:

        Country's taxation type (W or G).

        And picks the best available tax method (rate vs. fixed amount) for the best possible return.

#### Example API calls

##### Create a country

###### Request

`POST /api/countries/createCountry`

```json
{
  "name": "Hungary",
  "taxRate": 0.15,
  "taxType": "G"
}
```

###### Response

```json
{
    "data": {
        "id": 4,
        "name": "Hungary",
        "taxRate": 0.15,
        "taxAmount": null,
        "taxType": "G"
    },
    "message": "Country created successfully",
    "timestamp": "2025-04-27T11:09:26.760+00:00",
    "successful": true,
    "status": 200
}
```


##### Create a trader 

###### Request

`POST /api/traders/createTrader`

```json
{
  "name": "John Doe",
  "countryId": 1
}
```

###### Response

```json
{
    "data": {
        "id": 7,
        "name": "Trader X",
        "country": {
            "id": 1,
            "name": "Slovenia",
            "taxRate": 0.1,
            "taxAmount": 1.0,
            "taxType": "W"
        }
    },
    "message": "Trader created successfully",
    "timestamp": "2025-04-27T11:12:40.750+00:00",
    "successful": true,
    "status": 200
}
```


##### Calculate Possible Bet Return

###### Request

`POST /api/traders/calculatePossibleBetReturnAmount`

```json
{
    "traderId": "2",
    "playedAmount": 2,
    "odd": "1.5"
}
```

###### Response

```json
{
    "data": {
        "possibleReturnAmountBefTax": 3.0,
        "possibleReturnAmountAfterTax": 2.7,
        "taxRate": 0.1,
        "taxAmount": null
    },
    "message": null,
    "timestamp": "2025-04-27T11:15:26.132+00:00",
    "successful": true,
    "status": 200
}
```

##### Error Response Format example

```json
{
    "data": {
        "traderId": "must not be null"
    },
    "error": "Parameter validation failed",
    "timestamp": "2025-04-27T11:16:23.226+00:00",
    "successful": false,
    "status": 400
}
```