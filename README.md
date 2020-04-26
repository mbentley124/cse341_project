# cse341_project

Michael Bentley

## About the bank

We prefer it if the customers use a beet farm as colatoral (for no reason in particular, just who doesn't like beets)

Loan and credit cards openings need to be approved by a teller (not an atm teller either)

Loans over $100,000 require colatoral. 

No need to store the date the loan was opened in the database as that is already contained within the loan initial transaction. Same with amount loaned. 

A user can only have loan colatoral with a name that is up to fifteen characters long due to the size of the varchar in the db. This could be increased but we believe 15 characters is sufficient to express what the colatoral is. 

A user can only have $1,000,000 due total for loans. This isn't a database constraint since we are willing to loan more money but that would require more vetting not possible within this interface (i.e. talk with the ceo or something)

The cash a user receives from a loan is mailed to them as a check. (very secure)

An ATM is considered a teller, with a unique one for each location. 

Although it is not required in the database, in order to promote ease of access every user must have a unique name. This allows users to enter their name to login instead of memorizing their user id. We have found users are less likely to forget their name for some reason. 

Deposits are with a specific teller, but withdraws are with a specific location. This is since a teller is required for depositing, but only an ATM is required for withdraws (which all of our locations have). 

Does not allow clients to pay off their loans with a credit card (we like to promote better financial decisions)

Tellers can only work at one location

We only allow deposits into accounts at locations that have a teller working there. 

Tellers can be both employees and customers. 

Card purchase is slightly different from other relationships (ternary) since you can only make purchases at vendors with a card and vendors can only have purchases by cards (obviously you can make purchases at vendors with cash too, but banks wouldn't be involved with that at all). 

## Useful Files

### DDL.sql
Resets all of the tables, dropping the old versions and inserting the new ones. 

### DataGenerator.java
Fills the table with semi-random data. All the names are predetermined however any number value is randomly determined. The randomness is seeded so it should be consistent as to the random values (however if something using random is changed then everything after it will become different then what it was before)