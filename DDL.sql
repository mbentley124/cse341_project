DROP TABLE account_holder;
DROP TABLE account_withdraw;
DROP TABLE account_deposit;
DROP TABLE loan_payment;
DROP TABLE credit_card_payment;
DROP TABLE cash_transaction;
DROP TABLE card_purchase;
DROP TABLE secured_loan;
DROP TABLE loan;
DROP TABLE debit_card;
DROP TABLE credit_card;
DROP TABLE card;
DROP TABLE vendor;
DROP TABLE savings;
DROP TABLE checking;
DROP TABLE account;
DROP TABLE teller;
DROP TABLE customer;
DROP TABLE person;
DROP TABLE location;
DROP TABLE transaction;


-- TRANSACTION TABLE
CREATE TABLE transaction
    (t_id           NUMBER GENERATED BY DEFAULT AS IDENTITY,
    amount          NUMBER CHECK (amount > 0),
    t_date          TIMESTAMP,
    PRIMARY KEY (t_id));
    
-- BANK LOCATION TABLE
CREATE TABLE location
    (loc_id         NUMBER GENERATED BY DEFAULT AS IDENTITY,
    loc_name        VARCHAR (12),
    PRIMARY KEY (loc_id));

-- PEOPLE RELATED TABLES
CREATE TABLE person
    (p_id           NUMBER GENERATED BY DEFAULT AS IDENTITY,
    full_name       VARCHAR (25),
    PRIMARY KEY (p_id));
    
CREATE TABLE customer
    (p_id           NUMBER,
    joined_date     TIMESTAMP,
    PRIMARY KEY (p_id),
    FOREIGN KEY (p_id) REFERENCES person
        ON DELETE CASCADE);

CREATE TABLE teller
    (p_id           NUMBER,
    teller_loc_id   NUMBER,
    wage            NUMBER CHECK (wage >= 0),
    PRIMARY KEY (p_id),
    FOREIGN KEY (p_id) REFERENCES person
        ON DELETE CASCADE,
    FOREIGN KEY (teller_loc_id) REFERENCES location (loc_id)
        ON DELETE SET NULL);

-- THE ACCOUNT TABLES
CREATE TABLE account
    (acc_id             NUMBER GENERATED BY DEFAULT AS IDENTITY,
    balance             NUMBER CHECK (balance >= 0),
    acc_interest_rate   NUMBER,
    PRIMARY KEY (acc_id));

CREATE TABLE checking
    (acc_id         NUMBER,
    PRIMARY KEY (acc_id),
    FOREIGN KEY (acc_id) REFERENCES account
        ON DELETE CASCADE);

CREATE TABLE savings
    (acc_id             NUMBER,
    minimum_balance     NUMBER CHECK (minimum_balance >= 0),
    penalty             NUMBER CHECK (penalty >= 0),
    PRIMARY KEY (acc_id),
    FOREIGN KEY (acc_id) REFERENCES account
        ON DELETE CASCADE);

-- VENDOR TABLE
CREATE TABLE vendor
    (v_id           NUMBER GENERATED BY DEFAULT AS IDENTITY,
    vendor_name     VARCHAR (30),
    PRIMARY KEY (v_id));

-- CREDIT/DEBIT CARD TABLES
CREATE TABLE card
    (card_id            NUMBER GENERATED BY DEFAULT AS IDENTITY,
    card_holder_id      NUMBER,
    card_name           VARCHAR (30),
    card_opened_date    TIMESTAMP,
    PRIMARY KEY (card_id),
    FOREIGN KEY (card_holder_id) REFERENCES customer (p_id)
        ON DELETE SET NULL);
    
CREATE TABLE credit_card
    (card_id                NUMBER,
    credit_interest_rate    NUMBER,
    credit_limit            NUMBER CHECK (credit_limit > 0),
    balance_due             NUMBER,
    rolling_balance         NUMBER,
    CHECK (rolling_balance < credit_limit),
    PRIMARY KEY (card_id),
    FOREIGN KEY (card_id) REFERENCES card
        ON DELETE CASCADE);

CREATE TABLE debit_card
    (card_id        NUMBER,
    acc_id          NUMBER,
    PRIMARY KEY (card_id),
    FOREIGN KEY (card_id) REFERENCES card
        ON DELETE CASCADE,
    FOREIGN KEY (acc_id) REFERENCES checking
        ON DELETE CASCADE);

-- LOAN TABLES
CREATE TABLE loan 
    (l_id               NUMBER GENERATED BY DEFAULT AS IDENTITY,
    loanholder_id       NUMBER,
    loan_interest_rate  NUMBER,
    amount_loaned       NUMBER CHECK (amount_loaned > 0),
    amount_due          NUMBER CHECK (amount_due > 0),
    monthly_payment     NUMBER CHECK (monthly_payment > 0),
    PRIMARY KEY (l_id),
    FOREIGN KEY (loanholder_id) REFERENCES customer (p_id)
        ON DELETE SET NULL);

CREATE TABLE secured_loan
    (l_id           NUMBER,
    collatoral      VARCHAR (15),
    PRIMARY KEY (l_id),
    FOREIGN KEY (l_id) REFERENCES loan
        ON DELETE CASCADE);


-- ALL RELATIONSHIP SETS NOT ALREADY BUILT INTO EARLIER RELATIONS
CREATE TABLE card_purchase
    (t_id           NUMBER,
    card_id         NUMBER,
    v_id            NUMBER,
    PRIMARY KEY (t_id),
    FOREIGN KEY (card_id) REFERENCES card
        ON DELETE SET NULL,
    FOREIGN KEY (v_id) REFERENCES vendor
        ON DELETE SET NULL,
    FOREIGN KEY (t_id) REFERENCES transaction
        ON DELETE CASCADE);

CREATE TABLE credit_card_payment
    (t_id           NUMBER,
    card_id         NUMBER,
    PRIMARY KEY (t_id),
    FOREIGN KEY (card_id) REFERENCES credit_card
        ON DELETE CASCADE,
    FOREIGN KEY (t_id) REFERENCES transaction
        ON DELETE CASCADE);
        
CREATE TABLE loan_payment
    (t_id           NUMBER,
    l_id            NUMBER,
    PRIMARY KEY (t_id),
    FOREIGN KEY (l_id) REFERENCES loan
        ON DELETE CASCADE,
    FOREIGN KEY (t_id) REFERENCES transaction
        ON DELETE CASCADE);

CREATE TABLE cash_transaction
    (t_id           NUMBER,
    loc_id          NUMBER,
    PRIMARY KEY (t_id),
    FOREIGN KEY (t_id) REFERENCES transaction
        ON DELETE CASCADE,
    FOREIGN KEY (loc_id) REFERENCES location
        ON DELETE CASCADE);

CREATE TABLE account_deposit
    (t_id           NUMBER,
    acc_id          NUMBER,
    p_id            NUMBER,
    PRIMARY KEY (t_id),
    FOREIGN KEY (t_id) REFERENCES transaction
        ON DELETE CASCADE,
    FOREIGN KEY (acc_id) REFERENCES account
        ON DELETE CASCADE,
    FOREIGN KEY (p_id) REFERENCES teller
        ON DELETE CASCADE);
        
CREATE TABLE account_withdraw
    (t_id           NUMBER,
    acc_id          NUMBER,
    p_id          NUMBER,
    PRIMARY KEY (t_id),
    FOREIGN KEY (t_id) REFERENCES transaction
        ON DELETE CASCADE,
    FOREIGN KEY (acc_id) REFERENCES account
        ON DELETE CASCADE,
    FOREIGN KEY (p_id) REFERENCES teller
        ON DELETE CASCADE);

CREATE TABLE account_holder
    (acc_id         NUMBER,
    p_id            NUMBER,
    PRIMARY KEY (acc_id, p_id),
    FOREIGN KEY (acc_id) REFERENCES account
        ON DELETE CASCADE,
    FOREIGN KEY (p_id) REFERENCES customer
        ON DELETE CASCADE);
