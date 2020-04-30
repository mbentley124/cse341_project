# Credit to https://www.cs.swarthmore.edu/~newhall/unixhelp/javamakefiles.html for makefile framework.
JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

SOURCES = \
	mlb222/Bank.java \
	mlb222/CardPurchaseInterface.java \
	mlb222/DepositWithdrawInterface.java \
	mlb222/DepositWithdrawBackMethod.java \
	mlb222/LoanTakeoutInterface.java \
	mlb222/LoanTakeoutBackMethod.java \
	mlb222/utilities/ResultSetConverter.java \
	mlb222/utilities/Input.java \
	mlb222/utilities/CustomInputCondition.java \
	mlb222/utilities/Search.java \
	mlb222/utilities/ConnectionManager.java \
	mlb222/utilities/database_structures/Account.java \
	mlb222/utilities/database_structures/Card.java \
	mlb222/utilities/database_structures/CheckingAccount.java \
	mlb222/utilities/database_structures/CreditCard.java \
	mlb222/utilities/database_structures/Customer.java \
	mlb222/utilities/database_structures/DebitCard.java \
	mlb222/utilities/database_structures/Location.java \
	mlb222/utilities/database_structures/Person.java \
	mlb222/utilities/database_structures/SavingsAccount.java \
	mlb222/utilities/database_structures/Teller.java \
	mlb222/utilities/database_structures/Vendor.java \

CLASSES = $(SOURCES:.java=.class)

default: classes

classes: $(SOURCES:.java=.class)

jar:
	jar cfmv mlb222.jar mlb222/Manifest.txt $(CLASSES)

clean:
	find ./mlb222 -type f -name '*.class' -delete