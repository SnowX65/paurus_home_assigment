CREATE TABLE IF NOT EXISTS ALLOWED_COUNTRY_NAMES (
   name VARCHAR PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS COUNTRY (

    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
    name VARCHAR UNIQUE NOT NULL,
    tax_rate NUMERIC(3, 2) CHECK (tax_rate >= 0.00 AND tax_rate <= 0.99),
    tax_amount NUMERIC(20,2) CHECK(tax_amount >= 0.00),
    tax_type CHAR NOT NULL CHECK (tax_type IN ('W', 'G')) /* W = 'winnings', G = 'general' */
);

CREATE TABLE IF NOT EXISTS TRADER (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
    name VARCHAR NOT NULL,
    country_id INT NOT NULL,
    CONSTRAINT FK_TRADER_COUNTRY
        FOREIGN KEY (country_id) REFERENCES COUNTRY(id)
);

CREATE TABLE IF NOT EXISTS BETS (

    id     INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (increment by 100),
    match_id VARCHAR NOT NULL,
    market_id INT NOT NULL,
    outcome_id VARCHAR NOT NULL,
    specifiers VARCHAR,
    date_insert TIMESTAMP DEFAULT NOW()

);

DROP SEQUENCE  bets_id_seq;

CREATE SEQUENCE bets_id_seq START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS BETS (

    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('bets_id_seq'),
    insert_id BIGINT,
    match_id VARCHAR NOT NULL,
    market_id INT NOT NULL,
    outcome_id VARCHAR NOT NULL,
    specifiers VARCHAR,
    date_insert TIMESTAMP DEFAULT clock_timestamp()

);


