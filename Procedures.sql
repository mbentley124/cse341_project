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


CREATE OR REPLACE FUNCTION accountWithdraw (id IN account.acc_id%TYPE, amount IN account.balance%TYPE) RETURN savings.penalty%TYPE
AS
bal account.balance%TYPE;
min_bal savings.minimum_balance%TYPE;
penal savings.penalty%TYPE;
BEGIN
UPDATE account ac1
SET balance = (SELECT balance 
                FROM account ac2 
                WHERE ac2.acc_id = ac1.acc_id) - amount
WHERE ac1.acc_id = id;
SELECT balance INTO bal FROM account WHERE acc_id = id;
SELECT minimum_balance, penalty INTO min_bal, penal FROM account LEFT OUTER JOIN savings USING (acc_id) WHERE acc_id = id;

IF min_bal IS NULL THEN
    RETURN 0;
END IF;
IF bal < min_bal THEN
    UPDATE account ac1
    SET balance = (SELECT balance 
                FROM account ac2 
                WHERE ac2.acc_id = ac1.acc_id) - penal
    WHERE ac1.acc_id = id;
    return penal;
END IF;
return 0;
END;
/
