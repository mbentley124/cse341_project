import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class ValueGenerator {

	static String[] customerNames = new String[] { "Todd Packer", "Michael Scott", "Jim Halpert", "Pam Beasly",
			"Kevin Malone", "Meridith Palmer", "Angela Martin", "David Wallace", "Darrel Philbin", "Holly Flax",
			"Charles Miner" };

	public static void insertCustomer(String id, String name, Timestamp joined_timestamp) {
		// TODO
	}

	static String[] locationNames = new String[] { "Scranton", "Utica", "Stamford", "Nashua" };

	public static void insertLocation(String id, String name) {
		// TODO
	}

	static String[] employeeNames = new String[] { "Bob Vance", "Dwigt Schrute", "Michael Scott", "Andy Bernard" };

	public static void insertEmployee(String id, String name, String loc_id, double wage, boolean onlyEmployee) {
		// TODO insert into employee and person
	}

	static String[] vendorNames = new String[] { "Vance Refridgeration", "Dunder Mifflin", "Michael Scott Paper Company",
			"Schrute Farms" };

	public static void insertVendor(String id, String name) {
		// TODO
	}

	static String[] collatoralNames = new String[] { "Farm", "Sprinkles", "Refridgerator", "House" };

	public static void insertLoan(String id, String loanholder_id, double interest_rate, int loan_amount,
			int monthly_payment, String collatoral) {
		// TODO insert into loan and secured_loan if collatoral isn't null.
	}

	public static void insertAccount(String id, double balance, double interest_rate, Integer minimum_balance,
			Integer penalty, String[] account_holders) {
		// TODO insert into account and savings if minimum_balance and penalty isn't
		// null otherwise checking.
	}

	static String[] cardNames = new String[] { "Corporate", "Schrute Bucks", "Stanley Nickels", "Unicorns", "Leprechauns",
			"Dunder", "Casino Night", "Sabre", "Primary", "Secondary", "Special Projects", "Athlead", "Costco" };

	public static void insertCard(String id, String name, Timestamp opened_date) {
		// TODO
	}

	public static void insertDebitCard(String id, String name, Timestamp opened_date, String account_id) {
		insertCard(id, name, opened_date);
		// TODO
	}

	public static void insertCreditCard(String id, String name, Timestamp opened_date, String cardholder_id,
			double credit_interest_rate, int credit_limit, double balance_due, double rolling_balance) {
		insertCard(id, name, opened_date);
		// TODO
	}

	public static final int TOTAL_LOANS = 10;
	public static final int TOTAL_ACCOUNTS = 25;

	public static void main(String[] args) {
		// Insert vendors
		for (int i = 0; i < vendorNames.length; i++) {
			insertVendor(String.valueOf(i), vendorNames[i]);
		}

		// Generate customers.
		for (int i = 0; i < customerNames.length; i++) {
			insertCustomer(String.valueOf(i), customerNames[i], generateRandomTimestampBetweenYears(2000, 2010));
		}

		// Insert locations and their tellers.
		for (int i = 0; i < locationNames.length; i++) {
			insertLocation(String.valueOf(i), locationNames[i]);
			if (i + 1 == locationNames.length) {
				for (int x = i - 1; x < employeeNames.length; x++) {
					insertEmployee(String.valueOf(x + customerNames.length - 1), employeeNames[x], String.valueOf(i),
							(Math.random() + 1) * 10, false);
				}
			} else if (i > 0) {
				insertEmployee(String.valueOf(i - 1 + (customerNames.length - 1)), employeeNames[i - 1], String.valueOf(i),
						(Math.random() + 1) * 10, i == 1);
			}
		}

		// Insert TOTAL_LOANS loans
		for (int i = 0; i < TOTAL_LOANS; i++) {
			String collatoral = null;
			if (Math.random() > 0.5) {
				collatoral = getRandomArrayVal(collatoralNames);
			}
			String loanholder_id = String.valueOf(getRandomIndex(customerNames));
			double loan_interest = Math.random() * 10;
			int amount_loaned = (int) (Math.random() * 50000) + 5000;
			int monthly_payment = (int) (Math.random() * 200) + 50;
			insertLoan(String.valueOf(i), loanholder_id, loan_interest, amount_loaned, monthly_payment, collatoral);
		}

		// Insert TOTAL_ACCOUNTS accounts (savings and checking)
		for (int i = 0; i < TOTAL_ACCOUNTS; i++) {
			int balance = (int) (Math.random() * 4000) + 400;
			double interest_rate = Math.random() * 0.001;

			Integer minimum_balance = null;
			Integer penalty = null;
			if (Math.random() > 0.5) {
				minimum_balance = (int) Math.round(Math.random() * 300);
				penalty = (int) Math.round((Math.random() * 25) + 5);
			}

			String[] accountHolders;

			if (Math.random() > 0.7) {
				// Two account holders
				accountHolders = new String[] { String.valueOf(getRandomIndex(customerNames)),
						String.valueOf(getRandomIndex(customerNames)) };
				// If they are two of the same account holders then reduce array to be size one.
				if (accountHolders[0].equals(accountHolders[1])) {
					accountHolders = new String[] { accountHolders[0] };
				}
			} else {
				// One account holder
				accountHolders = new String[] { String.valueOf(getRandomIndex(customerNames)) };
			}

			insertAccount(String.valueOf(i), balance, interest_rate, minimum_balance, penalty, accountHolders);
		}

		// Insert all the cards.
		for (int i = 0; i < cardNames.length; i++) {
			String id = String.valueOf(i);
			String card_name = cardNames[i];
			Timestamp opened_timestamp = generateRandomTimestampBetweenYears(2011, 2017);
			if (Math.random() > 0.5) {
				// Credit card
				String cardholder_id = String.valueOf(getRandomIndex(customerNames));
				double credit_interest_rate = (Math.random() * 12) + 1;
				int credit_limit = (int) (Math.floor(Math.random() * 5) + 1) * 500;
				double balance_due = Math.random() * 500;
				double rolling_balance = Math.random() * 500 + balance_due;
				insertCreditCard(id, card_name, opened_timestamp, cardholder_id, credit_interest_rate, credit_limit,
						balance_due, rolling_balance);
			} else {
				// Debit card
				String account_id = String.valueOf(Math.floor(Math.random() * TOTAL_ACCOUNTS));
				insertDebitCard(id, card_name, opened_timestamp, account_id);
			}
		}

		// TODO insert transactions.
	}

	/**
	 * Returns random value from an array. Based off
	 * https://stackoverflow.com/a/8065554
	 */
	public static <T> T getRandomArrayVal(T[] array) {
		return array[getRandomIndex(array)];
	}

	public static int getRandomIndex(Object[] array) {
		return new Random().nextInt(array.length);
	}

	// Generates random timestamp between two specified years.
	public static Timestamp generateRandomTimestampBetweenYears(int startYear, int endYear) {
		GregorianCalendar.getInstance().set(startYear, 1, 1);
		Timestamp startTimestamp = new Timestamp(GregorianCalendar.getInstance().getTime().getTime());

		GregorianCalendar.getInstance().set(endYear, 1, 1);
		Timestamp endTimestamp = new Timestamp(GregorianCalendar.getInstance().getTime().getTime());
		return generateRandomTimestampBetween(startTimestamp, endTimestamp);
	}

	/**
	 * Generates a random date in between the start and end date. Based off of
	 * https://stackoverflow.com/a/11016560
	 */
	public static Timestamp generateRandomTimestampBetween(Timestamp start, Timestamp end) {
		long offsetTime = start.getTime();
		long endTime = end.getTime();
		long diff = endTime - offsetTime + 1;
		return new Timestamp(offsetTime + (long) (Math.random() * diff));
	}
}