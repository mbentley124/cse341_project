import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class DataGenerator {

	public static void insertPerson(String id, String name, Connection conn) {
		try (PreparedStatement insert_person = conn.prepareStatement("INSERT INTO person VALUES (?, ?)")) {
			insert_person.setString(1, id);
			insert_person.setString(2, name);
			insert_person.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String[] customerNames = new String[] { "Todd Packer", "Michael Scott", "Jim Halpert", "Pam Beasly",
			"Kevin Malone", "Meridith Palmer", "Angela Martin", "David Wallace", "Darrel Philbin", "Holly Flax",
			"Charles Miner" };

	public static void insertCustomer(String id, String name, Timestamp joined_timestamp, Connection conn) {
		insertPerson(id, name, conn);
		try (PreparedStatement insert_customer = conn.prepareStatement("INSERT INTO customer VALUES (?, ?)")) {
			insert_customer.setString(1, id);
			insert_customer.setTimestamp(2, joined_timestamp);
			insert_customer.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String[] employeeNames = new String[] { "Bob Vance", "Dwigt Schrute", "Michael Scott", "Andy Bernard" };

	public static void insertEmployee(String id, String name, String loc_id, double wage, boolean insertOnlyEmployee,
			Connection conn) {
		if (!insertOnlyEmployee) {
			insertPerson(id, name, conn);
		}
		try (PreparedStatement insert_teller = conn.prepareStatement("INSERT INTO teller VALUES (?, ?, ?)")) {
			insert_teller.setString(1, id);
			insert_teller.setString(2, loc_id);
			insert_teller.setDouble(3, wage);
			insert_teller.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String[] locationNames = new String[] { "Scranton", "Utica", "Stamford", "Nashua" };

	public static void insertLocation(String id, String name, Connection conn) {
		try (PreparedStatement insert_location = conn.prepareStatement("INSERT INTO location VALUES (?, ?)")) {
			insert_location.setString(1, id);
			insert_location.setString(2, name);
			insert_location.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String[] vendorNames = new String[] { "Vance Refridgeration", "Dunder Mifflin", "Michael Scott Paper Company",
			"Schrute Farms" };

	public static void insertVendor(String id, String name, Connection conn) {
		try (PreparedStatement insert_vendor = conn.prepareStatement("INSERT INTO vendor VALUES (?, ?)")) {
			insert_vendor.setString(1, id);
			insert_vendor.setString(2, name);
			insert_vendor.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String[] collatoralNames = new String[] { "Farm", "Sprinkles", "Refridgerator", "House" };

	public static void insertLoan(String id, String loanholder_id, double interest_rate, int loan_amount, int amount_due,
			int monthly_payment, String collatoral, Connection conn) {
		try (PreparedStatement insert_loan = conn.prepareStatement("INSERT INTO loan VALUES (?, ?, ?, ?, ?, ?)");
				PreparedStatement insert_collatoral = conn.prepareStatement("INSERT INTO secured_loan VALUES (?, ?)")) {
			insert_loan.setString(1, id);
			insert_loan.setString(2, loanholder_id);
			insert_loan.setDouble(3, interest_rate);
			insert_loan.setInt(4, loan_amount);
			insert_loan.setInt(5, amount_due);
			insert_loan.setInt(6, monthly_payment);
			insert_loan.execute();
			if (collatoral != null) {
				insert_collatoral.setString(1, id);
				insert_collatoral.setString(2, collatoral);
				insert_collatoral.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertAccount(String id, double balance, double interest_rate, Integer minimum_balance,
			Integer penalty, String[] account_holders, Connection conn) {
		try (PreparedStatement insert_account = conn.prepareStatement("INSERT INTO account VALUES (?, ?, ?)");
				PreparedStatement insert_checking = conn.prepareStatement("INSERT INTO checking VALUES (?)");
				PreparedStatement insert_savings = conn.prepareStatement("INSERT INTO savings VALUES (?, ?, ?)");
				PreparedStatement insert_account_holder = conn.prepareStatement("INSERT INTO account_holder VALUES (?, ?)")) {
			insert_account.setString(1, id);
			insert_account.setDouble(2, balance);
			insert_account.setDouble(3, interest_rate);
			insert_account.execute();
			if (penalty != null && minimum_balance != null) {
				insert_savings.setString(1, id);
				insert_savings.setInt(2, minimum_balance);
				insert_savings.setInt(3, penalty);
				insert_savings.execute();
			} else {
				insert_checking.setString(1, id);
				insert_checking.execute();
			}
			insert_account_holder.setString(1, id);
			for (String account_holder : account_holders) {
				insert_account_holder.setString(2, account_holder);
				insert_account_holder.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String[] cardNames = new String[] { "Corporate", "Schrute Bucks", "Stanley Nickels", "Unicorns", "Leprechauns",
			"Dunder", "Casino Night", "Sabre", "Primary", "Secondary", "Special Projects", "Athlead", "Costco" };

	public static void insertCard(String id, String name, Timestamp opened_date, Connection conn) {
		try (PreparedStatement insert_card = conn.prepareStatement("INSERT INTO card VALUES (?, ?, ?)")) {
			insert_card.setString(1, id);
			insert_card.setString(2, name);
			insert_card.setTimestamp(3, opened_date);
			insert_card.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertDebitCard(String id, String name, Timestamp opened_date, String account_id,
			Connection conn) {
		insertCard(id, name, opened_date, conn);
		try (PreparedStatement insert_debit_card = conn.prepareStatement("INSERT INTO debit_card VALUES (?, ?)")) {
			insert_debit_card.setString(1, id);
			insert_debit_card.setString(2, account_id);
			insert_debit_card.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertCreditCard(String id, String name, Timestamp opened_date, String cardholder_id,
			double credit_interest_rate, int credit_limit, double balance_due, double rolling_balance, Connection conn) {
		insertCard(id, name, opened_date, conn);
		try (PreparedStatement insert_credit_card = conn
				.prepareStatement("INSERT INTO credit_card VALUES (?, ?, ?, ?, ?, ?)")) {
			insert_credit_card.setString(1, id);
			insert_credit_card.setString(2, cardholder_id);
			insert_credit_card.setDouble(3, credit_interest_rate);
			insert_credit_card.setInt(4, credit_limit);
			insert_credit_card.setDouble(5, balance_due);
			insert_credit_card.setDouble(6, rolling_balance);
			insert_credit_card.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final int TOTAL_LOANS = 10;
	public static final int TOTAL_ACCOUNTS = 25;

	public static void main(String[] args) {
		try (Connection conn = ConnectionManager.connect()) {
			// Insert vendors
			for (int i = 0; i < vendorNames.length; i++) {
				insertVendor(String.valueOf(i), vendorNames[i], conn);
			}

			// Generate customers.
			for (int i = 0; i < customerNames.length; i++) {
				insertCustomer(String.valueOf(i), customerNames[i], generateRandomTimestampBetweenYears(2000, 2010), conn);
			}

			// Insert locations and their tellers.
			for (int i = 0; i < locationNames.length; i++) {
				insertLocation(String.valueOf(i), locationNames[i], conn);
				if (i + 1 == locationNames.length) {
					for (int x = i - 1; x < employeeNames.length; x++) {
						insertEmployee(String.valueOf(x + customerNames.length - 1), employeeNames[x], String.valueOf(i),
								(Math.random() + 1) * 10, false, conn);
					}
				} else if (i > 0) {
					insertEmployee(String.valueOf(i - 1 + (customerNames.length - 1)), employeeNames[i - 1], String.valueOf(i),
							(Math.random() + 1) * 10, i == 1, conn);
				}
			}

			List<String> checking_account_ids = new ArrayList<>();

			// Insert TOTAL_LOANS loans
			for (int i = 0; i < TOTAL_LOANS; i++) {
				String collatoral = null;
				if (Math.random() > 0.5) {
					collatoral = getRandomArrayVal(collatoralNames);
				}
				String loanholder_id = String.valueOf(getRandomIndex(customerNames));
				double loan_interest = Math.random() * 10;
				int amount_loaned = (int) (Math.random() * 50000) + 5000;
				int amount_due = amount_loaned - (int) (Math.random() * 5000);
				int monthly_payment = (int) (Math.random() * 200) + 50;
				insertLoan(String.valueOf(i), loanholder_id, loan_interest, amount_loaned, amount_due, monthly_payment,
						collatoral, conn);
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
				} else {
					checking_account_ids.add(String.valueOf(i));
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

				insertAccount(String.valueOf(i), balance, interest_rate, minimum_balance, penalty, accountHolders, conn);
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
							balance_due, rolling_balance, conn);
				} else {
					// Debit card
					String account_id = getRandomArrayVal(checking_account_ids.toArray()).toString();
					insertDebitCard(id, card_name, opened_timestamp, account_id, conn);
				}
			}
			// TODO insert transactions.
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}

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
		Timestamp startTimestamp = Timestamp.valueOf(startYear + "-01-01 00:00:00");

		Timestamp endTimestamp = Timestamp.valueOf(endYear + "-01-01 00:00:00");
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