Michael Bentley

# General Information

Modified checking/savings account from the ER diagram. Moved minimum balance/penalty to checking account where it belongs. 

At any time in an interface you can type back or quit to go back within the interface or quit the interface. Going back in an interface immediately after deciding to takeout another loan/purchase/account transaction will take you back to the input that came before your first time with that input prompt. 


# Deposit/Withdrawal Interface (Interface 2)

Michael Scott is a good customer to use for this (has both savings and checking accounts) 

You are able to withdraw money from your account with any teller (atms included)

Only human tellers are able to deposit money into your account

You can also transfer money between any of your two accounts using this interface (only with human tellers)

Penlaties are automatically enforced for overdraft of the account. 


# Card Purchase Interface (Interface 7)

Kevin Malone is a good person to use for this interface (Has credit and debit cards)

The options for vendors are: Vance Refridgeration, Dunder Mifflin, Michael Scott Paper Company, and Schrute Farms. 

I have the user search for their vendor even though there are only four of them, since for an actual bank there would be hundreds of possible vendors. 

The interface will prevent you from bring either your debit card's checking account balance below zero, or bring the rolling balance of your credit card above your credit limit. 

There is still a penalty imposed if you make a purchase with a debit card that brings its checking account's balance below the minimum. 


# Loan Interface (Interface 6)

Kevin Malone works well for this interface (although really anybody does. He has money for loans that is outstanding)

We prefer it if the customers use a beet farm as colatoral (for no reason in particular, we just like beets). Although Nickel Bank is a pawn shop of sorts and will approve anything to be used as colatoral. 

Loan openings need to be approved by a human teller (credit cards too for that matter)

In this interface we only list locations that are capable of giving out loans (Ones with human tellers)

Loans over $100,000 require colatoral. Below $100,000 do not but a discount on interest rate will be given if colatoral is used. 

The customer needs to provide their digital signature for the loan. (Nickel bank had some of its finest lawyers look over the interface to ensure the legally binding nature of the loan)

A user can only have loan colatoral with a name that is up to 60 characters long due to the size of the varchar in the db. This could be increased but we believe 60 characters is more than sufficient to express what the colatoral is. 

A user can only have $1,000,000 due total for loans. This isn't a database constraint since we are willing to loan more money but that would require more vetting not possible within this interface (i.e. talk with the ceo or something)

The user can either receive the money from their loan directly into their account or in the form of a check 

A users loan interest rate is calculated based on the amount of money they currently have stored in their accounts, and the amount of money outstanding in loans with us, with a discount if they decide to provide collatoral. (There is a cap on the minimum and maximum interest rate)

The monthly payment for a loan is just a percentage of the total loan amount. (Although there is a discount if your colatoral is a beet farm! We love beets!)


# Future Plans

Location is lost when somebody does a money transfer between accounts (you can tell the location through the teller only if they have not changed locations). This is the case for other types of transactions to. In the future I would give every transaction a designated location (would have to have a placeholder location for transactions like card purchases)

Store penalties being enforced for checking account overdraft fees. Currently when a checking account is charged with a penalty there is no recording of that happening. I need to add a relationship storing transaction penalties. 

Allow multiple people with their name on a loan/credit card. 

Generate realistic, unique card numbers as the ids for credit and debit cards. 

Allow customers to login using their id too, so we are able to have multiple customers with the same name. 


# Miscellaneous Files

### DDL.sql
Resets all of the tables, dropping the old versions and inserting the new ones. 

### Procedures.sql
Contains all the procedures/functions that are used in the database. Mostly relating to adjusting the balance for accounts/cards. 

### DataGenerator.java
Fills the table with semi-random data. All the names are predetermined however any number value is randomly determined. The randomness is seeded so it should be consistent as to the random values (however if something using random is changed then everything after it will become different then what it was before)

### database_structures package
Contains wrappers for most of the database tables. Most wrapper classes have useful methods within it for querying the database for values relevant to that specific entity within the database. 

### ResultSetConverter.java
Converts ResultSet to their relevant database structure wrapper class. 

### ConnectionManager.java
Does the database querying that is not specific to a particular row within the database (i.e. inserting a new row)

### Input.java
Used for easily managing user input. 

### compile.sh
Recompiles all the java code. 