# cse341_project

Michael Bentley

## About the bank

### General Database Information

My ER diagram had savings accounts with a minimum balance and penalty for going below, instead of checking accounts. This is fixed in the final database so checking accounts now have a minimum balance and penalty instead. (I like to think I was subconsciously trying to confuse my customers by mislabeling their accounts as a way of embracing the Nickel Bank mentality)

An ATM is considered a teller in terms of the database, with a unique one for each location. 

We don't have a trigger to automatically adjust a users balance after an account_deposit/account_withdrawal entry is inserted. This is on purpose as this will allow the pl/sql function that is set up to return the penalty for withdrawal from savings accounts. Otherwise that would have to be calculated after the transaction which would be a little fickle. Then for consistencies sake deposits this is also not set up for account_deposit. There is pl/sql which makes account balance adjustment easy enough. 

Tellers can only work at one location

Card purchase is slightly different from other relationships (ternary) since you can only make purchases at vendors with a card and vendors can only have purchases by cards (obviously you can make purchases at vendors with cash too, but banks wouldn't be involved with that at all). 


### General Interface Information

Although it is not required in the database, in order to promote ease of access every user must have a unique name. This allows users to enter their name to login instead of memorizing their user id. We have found users are less likely to forget their name for some reason (not sure why)


### Deposit/Withdrawal Interface (Interface 2)

Michael Scott is a good customer to use for this (has both savings and checking accounts) 

You are able to withdraw money from your account with any teller (atms included)

Only human tellers are able to deposit money into your account

Any withdrawal/deposit registers the money being given at the location specified. 

You can also transfer money between any of your two accounts using this interface (only with human tellers)

Account money transfers are stored in the database as one transaction connected to both account_withdrawal and account_deposit (instead of those two being cash_transaction as is the case for plain deposits/withdrawals)

I realize that you can't tell the location of a money transfer between accounts if tellers are allowed to change the location they work at (since all you know for the transaction is the teller who approved the transaction and where they work at). So this means tellers are not allowed to change locations. (This could rather easily be fixed by requiring transactions to have a location attached to them. This would also remove the need for the cash_transaction relationship set. However since at this time I have already submitted by ER diagram I don't want to make too many changes and this has no direct impact on the interfaces implemented)


### Card Purchase Interface (Interface 7)

Kevin Malone is a good person to use for this interface

The options for vendors are: Vance Refridgeration, Dunder Mifflin, Michael Scott Paper Company, and Schrute Farms.

I have the user search for their vendor even though there are only four of them, since for an actual bank there would be hundreds of possible vendors. 

The interface will prevent you from bring either your debit card's checking account balance below zero, or bring the rolling balance of your credit card above your credit limit. 

There is still a penalty imposed if you make a purchase with a debit card that brings its balance below the minimum. 


### Loan Interface (Interface 6)

Kevin Malone works well for this interface (although really anybody does. He has money for loans that is outstanding)

We prefer it if the customers use a beet farm as colatoral (for no reason in particular, we just like beets)

Loan openings need to be approved by a human teller (credit cards too for that matter)

In this interface we only list locations that are capable of giving out loans (Ones with human tellers)

Loans over $100,000 require colatoral. 

Nickel bank is a pawn shop of sorts. We allow anything to be used as colatoral (got an old trombone, that works!) Even though tellers in the interface are set up to approve all colatoral, we do keep track of the teller that approved the loan/colatoral to ensure accountability. 

The customer needs to provide their digital signature for the loan. (Nickel bank had some of its finest lawyers look over the interface to ensure the legally binding nature of the loan)

No need to store the date the loan was opened in the database as that is already contained within the loan initial transaction. Same with amount loaned. 

A user can only have loan colatoral with a name that is up to 60 characters long due to the size of the varchar in the db. This could be increased but we believe 60 characters is more than sufficient to express what the colatoral is. 

A user can only have $1,000,000 due total for loans. This isn't a database constraint since we are willing to loan more money but that would require more vetting not possible within this interface (i.e. talk with the ceo or something)

The user can either receive the money from their loan directly into their account or in the form of a check (I was hoping it would be dollars instead of a check so we can give people a wheelbarrow full of cash as their loan, but management decided that was unreasonable for some reason). 

A users loan interest rate is calculated based on the amount of money they currently have stored in their accounts, and the amount of money outstanding in loans with us. The user is given a discount in interest rate if they decide to provide collatoral. (There is a cap on the minimum and maximum interest rate)

The monthly payment for a loan is just a percentage of the total loan amount. (Although there is a discount if your colatoral is a beet farm! We love beets!)



## Interesting Files

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