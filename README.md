# cse341_project

## About the bank

An ATM is considered a teller, with a unique one for each location. 

Although it is not required in the database, in order to promote ease of access every user must have a unique name. This allows users to enter their name to login instead of memorizing their user id. We have found users are less likely to forget their name for some reason. 

Deposits are with a specific teller, but withdraws are with a specific location. This is since a teller is required for depositing, but only an ATM is required for withdraws (which all of our locations have). 

Does not allow clients to pay off their loans with a credit card (we like to promote better financial decisions)

Tellers can only work at one location

We only allow deposits into accounts at locations that have a teller working there. 

Tellers can be both employees and customers. 

## Useful Files

### DDL.sql
Resets all of the tables, dropping the old versions and inserting the new ones. 

### DataGenerator.java
Fills the table with semi-random data. All the names are predetermined however any number value is randomly determined. 