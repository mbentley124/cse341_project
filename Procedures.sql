--drop function accountDeposit;

CREATE OR REPLACE PROCEDURE accountDeposit (id in account.acc_id%type, amount in account.balance%type)
AS
BEGIN
    UPDATE account ac1
    SET balance = (SELECT balance 
                   FROM account ac2 
                   WHERE ac2.acc_id = ac1.acc_id) + amount
    WHERE ac1.acc_id = id;
end;
/

CREATE OR REPLACE PROCEDURE creditCardPurchase (id in credit_card.card_id%type, amount in credit_card.rolling_balance%type)
AS
BEGIN
    UPDATE credit_card c1
    SET rolling_balance = (SELECT rolling_balance 
                   FROM credit_card c2 
                   WHERE c2.card_id = c1.card_id) + amount
    WHERE c1.card_id = id;
end;
/


CREATE OR REPLACE FUNCTION accountWithdraw (id IN account.acc_id%TYPE, amount IN account.balance%TYPE) 
RETURN savings.penalty%TYPE
AS
    bal account.balance%TYPE;
    min_bal savings.minimum_balance%TYPE;
    penal savings.penalty%TYPE;
BEGIN
    accountDeposit(id, -amount);

    SELECT balance INTO bal FROM account WHERE acc_id = id;

    SELECT minimum_balance, penalty INTO min_bal, penal FROM account LEFT OUTER JOIN savings USING (acc_id) WHERE acc_id = id;

    IF min_bal IS NULL THEN
        RETURN 0;
    END IF;
    IF bal < min_bal THEN
        accountDeposit(id, -penal);
        return penal;
    END IF;
    return 0;
END;
/
