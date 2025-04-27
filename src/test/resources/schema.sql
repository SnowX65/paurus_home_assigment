CREATE TABLE IF NOT EXISTS ALLOWED_COUNTRY_NAMES (
                                                     name VARCHAR PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS COUNTRY (

                                       id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
                                       name VARCHAR UNIQUE NOT NULL,
                                       tax_rate NUMERIC(3, 2) CHECK (taxRate >= 0.00 AND taxRate <= 0.99),
    tax_amount NUMERIC(20,2) CHECK(taxAmount >= 0.00),
    tax_type CHAR NOT NULL CHECK (taxType IN ('W', 'G')) /* W = 'winnings', G = 'general' */
    );

CREATE TABLE IF NOT EXISTS TRADER (
                                      id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
                                      name VARCHAR NOT NULL,
                                      country_id INT NOT NULL,
                                      CONSTRAINT FK_TRADER_COUNTRY
                                      FOREIGN KEY (country_id) REFERENCES COUNTRY(id)
    );



