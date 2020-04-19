import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import utilities.ConnectionManager;

public class DataGenerator {

	private static Random random = new Random(20);

	public static long insertPerson(String name, Connection conn) {
		try (PreparedStatement insert_person = conn.prepareStatement("INSERT INTO person (full_name) VALUES (?)",
				new String[] { "p_id" })) {
			insert_person.setString(1, name);
			insert_person.execute();
			ResultSet results = insert_person.getGeneratedKeys();
			results.next();
			return results.getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	static String[] customerNames = new String[] { "Todd Packer", "Michael Scott", "Jim Halpert", "Pam Beasly",
			"Kevin Malone", "Meridith Palmer", "Angela Martin", "David Wallace", "Darrel Philbin", "Holly Flax",
			"Charles Miner" };

	public static long insertCustomer(String name, Timestamp joined_timestamp, Connection conn) {
		long id = insertPerson(name, conn);
		try (PreparedStatement insert_customer = conn.prepareStatement("INSERT INTO customer VALUES (?, ?)")) {
			insert_customer.setLong(1, id);
			insert_customer.setTimestamp(2, joined_timestamp);
			insert_customer.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	static String[] employeeNames = new String[] { "Bob Vance", "Dwigt Schrute", "Michael Scott", "Andy Bernard" };

	public static long insertEmployee(long id, String name, long loc_id, double wage, boolean insertOnlyEmployee,
			Connection conn) {
		if (!insertOnlyEmployee) {
			id = insertPerson(name, conn);
		}
		try (PreparedStatement insert_teller = conn.prepareStatement("INSERT INTO teller VALUES (?, ?, ?)")) {
			insert_teller.setLong(1, id);
			insert_teller.setLong(2, loc_id);
			insert_teller.setDouble(3, wage);
			insert_teller.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	static String[] locationNames = new String[] { "Scranton", "Utica", "Stamford", "Nashua" };

	public static long insertLocation(String name, Connection conn) {
		try (PreparedStatement insert_location = conn.prepareStatement("INSERT INTO location (loc_name) VALUES (?)",
				new String[] { "loc_id" })) {
			insert_location.setString(1, name);
			insert_location.execute();
			ResultSet results = insert_location.getGeneratedKeys();
			results.next();
			return results.getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	static String[] vendorNames = new String[] { "Vance Refridgeration", "Dunder Mifflin", "Michael Scott Paper Company",
			"Schrute Farms" };

	public static long insertVendor(String name, Connection conn) {
		try (PreparedStatement insert_vendor = conn.prepareStatement("INSERT INTO vendor (vendor_name) VALUES (?)",
				new String[] { "v_id" })) {
			insert_vendor.setString(1, name);
			insert_vendor.execute();
			ResultSet results = insert_vendor.getGeneratedKeys();
			results.next();
			return results.getLong(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	static String[] collatoralNames = new String[] { "Farm", "Sprinkles", "Refridgerator", "House" };

	public static long insertAccount(double balance, double interest_rate, Integer minimum_balance, Integer penalty,
			long[] account_holders, Connection conn) {
		try (
				PreparedStatement insert_account = conn.prepareStatement(
						"INSERT INTO account (balance, acc_interest_rate) VALUES (?, ?)", new String[] { "acc_id" });
				PreparedStatement insert_checking = conn.prepareStatement("INSERT INTO checking VALUES (?)");
				PreparedStatement insert_savings = conn.prepareStatement("INSERT INTO savings VALUES (?, ?, ?)");
				PreparedStatement insert_account_holder = conn.prepareStatement("INSERT INTO account_holder VALUES (?, ?)")) {
			insert_account.setDouble(1, balance);
			insert_account.setDouble(2, interest_rate);
			insert_account.execute();
			ResultSet results = insert_account.getGeneratedKeys();
			results.next();
			long account_id = results.getLong(1);
			if (penalty != null && minimum_balance != null) {
				insert_savings.setLong(1, account_id);
				insert_savings.setInt(2, minimum_balance);
				insert_savings.setInt(3, penalty);
				insert_savings.execute();
			} else {
				insert_checking.setLong(1, account_id);
				insert_checking.execute();
			}
			insert_account_holder.setLong(1, account_id);
			for (long account_holder : account_holders) {
				insert_account_holder.setLong(2, account_holder);
				insert_account_holder.execute();
			}
			return account_id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	static String[] cardNames = new String[] { "Corporate", "Schrute Bucks", "Stanley Nickels", "Unicorns", "Leprechauns",
			"Dunder", "Casino Night", "Sabre", "Primary", "Secondary", "Special Projects", "Athlead", "Costco" };

	public static long insertCard(String name, long card_holder_id, Timestamp opened_date, Connection conn) {
		try (PreparedStatement insert_card = conn.prepareStatement(
				"INSERT INTO card (card_holder_id, card_name, card_opened_date) VALUES (?, ?, ?)",
				new String[] { "card_id" })) {
			insert_card.setLong(1, card_holder_id);
			insert_card.setString(2, name);
			insert_card.setTimestamp(3, opened_date);
			insert_card.execute();
			ResultSet results = insert_card.getGeneratedKeys();
			results.next();
			return results.getLong(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static long insertDebitCard(String name, long card_holder_id, Timestamp opened_date, long account_id,
			Connection conn) {
		long card_id = insertCard(name, card_holder_id, opened_date, conn);
		try (PreparedStatement insert_debit_card = conn.prepareStatement("INSERT INTO debit_card VALUES (?, ?)")) {
			insert_debit_card.setLong(1, card_id);
			insert_debit_card.setLong(2, account_id);
			insert_debit_card.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return card_id;
	}

	public static long insertCreditCard(String name, Timestamp opened_date, long cardholder_id,
			double credit_interest_rate, int credit_limit, double balance_due, double rolling_balance, Connection conn) {
		long card_id = insertCard(name, cardholder_id, opened_date, conn);
		try (PreparedStatement insert_credit_card = conn
				.prepareStatement("INSERT INTO credit_card VALUES (?, ?, ?, ?, ?)")) {
			insert_credit_card.setLong(1, card_id);
			insert_credit_card.setDouble(2, credit_interest_rate);
			insert_credit_card.setInt(3, credit_limit);
			insert_credit_card.setDouble(4, balance_due);
			insert_credit_card.setDouble(5, rolling_balance);
			insert_credit_card.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return card_id;
	}

	public static void insertLoanPayment(long t_id, long l_id, Connection conn) {
		try (PreparedStatement insert = conn.prepareStatement("INSERT INTO loan_payment VALUES (?, ?)")) {
			insert.setLong(1, t_id);
			insert.setLong(2, l_id);
			insert.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertCreditCardPayment(long t_id, long cc_id, Connection conn) {
		try (PreparedStatement insert = conn.prepareStatement("INSERT INTO credit_card_payment VALUES (?, ?)")) {
			insert.setLong(1, t_id);
			insert.setLong(2, cc_id);
			insert.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final int TOTAL_LOANS = 10;
	public static final int TOTAL_ACCOUNTS = 25;
	public static final int TOTAL_TRANSACTIONS = 130;

	public static void main(String[] args) {
		try (Connection conn = ConnectionManager.connect()) {

			Long[] vendor_ids = new Long[vendorNames.length];
			// Insert vendors
			for (int i = 0; i < vendorNames.length; i++) {
				vendor_ids[i] = insertVendor(vendorNames[i], conn);
			}

			Long[] customer_ids = new Long[customerNames.length];
			// Generate customers.
			for (int i = 0; i < customerNames.length; i++) {
				customer_ids[i] = insertCustomer(customerNames[i], generateRandomTimestampBetweenYears(2000, 2010), conn);
			}

			List<Long> employee_ids = new ArrayList<>();
			Long[] atm_ids = new Long[locationNames.length];
			Long[] location_ids = new Long[locationNames.length];

			// Insert locations and their tellers.
			for (int i = 0; i < locationNames.length; i++) {
				long loc_id = insertLocation(locationNames[i], conn);
				long atm_id = insertEmployee(-1, locationNames[i] + " ATM", loc_id, 0, false, conn);
				atm_ids[i] = atm_id;
				location_ids[i] = loc_id;
				if (i + 1 == locationNames.length) {
					for (int x = i - 1; x < employeeNames.length; x++) {
						// int emp_id = x + customerNames.length - 1;
						employee_ids.add(insertEmployee(-1, employeeNames[x], loc_id, (random.nextDouble() + 1) * 10, false, conn));
					}
				} else if (i > 0) {
					long emp_id = -1;
					if (i == 1) {
						emp_id = customer_ids[customer_ids.length - 1];
					}
					employee_ids
							.add(insertEmployee(emp_id, employeeNames[i - 1], loc_id, (random.nextDouble() + 1) * 10, i == 1, conn));
				}
			}

			List<Long> teller_ids = new ArrayList<>(employee_ids);
			teller_ids.addAll(Arrays.asList(atm_ids));

			// From loans id to loan holder id.
			Map<Long, Long> loanholder_ids = new HashMap<>(); // long[] loanholder_ids = new long[TOTAL_LOANS];

			// Insert TOTAL_LOANS loans
			for (int i = 0; i < TOTAL_LOANS; i++) {
				String collatoral = null;
				if (random.nextDouble() > 0.5) {
					collatoral = getRandomArrayVal(collatoralNames);
				}
				long loanholder_id = getRandomArrayVal(customer_ids);
				double loan_interest = random.nextDouble() * 10;
				double amount_loaned = (random.nextDouble() * 50000) + 5000;
				double amount_due = amount_loaned - (random.nextDouble() * 5000);
				double monthly_payment = (random.nextDouble() * 200) + 50;
				long l_id = ConnectionManager.insertLoan(loanholder_id, loan_interest, amount_loaned, amount_due,
						monthly_payment, collatoral, conn);
				loanholder_ids.put(l_id, loanholder_id);
			}

			// From account holder id to their account ids.
			Map<Long, List<Long>> account_holder_ids = new HashMap<>();
			List<Long> checking_account_ids = new ArrayList<>();
			Long[] account_ids = new Long[TOTAL_ACCOUNTS];

			// Insert TOTAL_ACCOUNTS accounts (savings and checking)
			for (int i = 0; i < TOTAL_ACCOUNTS; i++) {
				int balance = (int) (random.nextDouble() * 4000) + 400;
				double interest_rate = random.nextDouble() * 0.001;

				Integer minimum_balance = null;
				Integer penalty = null;
				if (random.nextDouble() > 0.5) {
					minimum_balance = (int) Math.round(random.nextDouble() * 300);
					penalty = (int) Math.round((random.nextDouble() * 25) + 5);
				} // else {
					// checking_account_ids.add(String.valueOf(i));
				// }

				long[] accountHolders;

				if (random.nextDouble() > 0.7) {
					// Two account holders
					accountHolders = new long[] { getRandomArrayVal(customer_ids), getRandomArrayVal(customer_ids) };
					// If they are two of the same account holders then reduce array to be size one.
					if (accountHolders[0] == accountHolders[1]) {
						accountHolders = new long[] { accountHolders[0] };
					}
				} else {
					// One account holder
					accountHolders = new long[] { getRandomArrayVal(customer_ids) };
				}
				long acc_id = insertAccount(balance, interest_rate, minimum_balance, penalty, accountHolders, conn);
				account_ids[i] = acc_id;
				if (minimum_balance == null) {
					checking_account_ids.add(acc_id);
				}
				for (long account_holder : accountHolders) {
					List<Long> current_account_ids = account_holder_ids.getOrDefault(account_holder, new ArrayList<>());
					current_account_ids.add(acc_id);
					account_holder_ids.put(account_holder, current_account_ids);
				}
				// complete_account_holders[i] = accountHolders;
			}

			Map<Long, Long> credit_card_holders = new HashMap<>();
			Long[] card_ids = new Long[cardNames.length];

			// Insert all the cards.
			for (int i = 0; i < cardNames.length; i++) {
				// String id = String.valueOf(i);
				String card_name = cardNames[i];
				Timestamp opened_timestamp = generateRandomTimestampBetweenYears(2011, 2017);
				if (random.nextDouble() > 0.5) {
					// Credit card
					long cardholder_id = getRandomArrayVal(customer_ids);
					double credit_interest_rate = (random.nextDouble() * 12) + 1;
					int credit_limit = (int) (Math.floor(random.nextDouble() * 5) + 1) * 500;
					double balance_due = random.nextDouble() * 500;
					double rolling_balance = random.nextDouble() * 500 + balance_due;
					Long card_id = insertCreditCard(card_name, opened_timestamp, cardholder_id, credit_interest_rate,
							credit_limit, balance_due, rolling_balance, conn);
					credit_card_holders.put(card_id, cardholder_id);
					card_ids[i] = card_id;
				} else {
					// Debit card
					long account_id = getRandomArrayVal(checking_account_ids.toArray(new Long[0]));
					long accountholder_id = -1;
					for (Entry<Long, List<Long>> entry : account_holder_ids.entrySet()) {
						if (entry.getValue().contains(account_id)) {
							accountholder_id = entry.getKey();
						}
					}
					card_ids[i] = insertDebitCard(card_name, accountholder_id, opened_timestamp, account_id, conn);
				}
			}

			// Inserts TOTAL_TRANSACTIONS amount of transactions
			for (int i = 0; i < TOTAL_TRANSACTIONS; i++) {
				double amount = random.nextDouble() * 500 + 1;
				// Transactions happen between 2019 and today.
				Timestamp transaction_timestamp = generateRandomTimestampBetweenYears(2019, 2020);

				long t_id = ConnectionManager.insertTransaction(amount, transaction_timestamp, conn);

				// Values used on whether or not the transaction type has another side to it
				// (where the money comes from)
				Long account_owner_id = null;
				boolean insert_cash_side = false;

				// Determines the transaction type.
				double rand = random.nextDouble();
				if (rand < 0.2) {
					// Loan payment
					long loan_id = getRandomArrayVal(loanholder_ids.keySet().toArray(new Long[0]));
					insertLoanPayment(t_id, loan_id, conn);
					if (random.nextDouble() < 0.5) {
						// From cash
						insert_cash_side = true;
					} else {
						// From account.
						account_owner_id = loanholder_ids.get(loan_id);
					}
				} else if (rand < 0.4) {
					// Credit card payment
					Long card_id = getRandomArrayVal(credit_card_holders.keySet().toArray(new Long[0]));
					insertCreditCardPayment(t_id, card_id, conn);
					if (random.nextDouble() < 0.5) {
						insert_cash_side = true;
					} else {
						account_owner_id = credit_card_holders.get(card_id);
					}
				} else if (rand < 0.6) {
					// Credit card purchase. No other side to the transaction.
					Long card_id = getRandomArrayVal(card_ids);
					Long vendor_id = getRandomArrayVal(vendor_ids);

					ConnectionManager.insertCardPurchase(t_id, card_id, vendor_id, conn);
				} else if (rand < 0.8) {
					// Account deposit.

					long acc_id = getRandomArrayVal(account_ids);
					long emp_id = getRandomArrayVal(employee_ids.toArray(new Long[0]));
					ConnectionManager.insertAccountDeposit(t_id, acc_id, emp_id, conn);
					// Always uses cash.
					insert_cash_side = true;
				} else {
					// Withdrawing money from an account. No need for other side of transaction
					// since it has to be cash.
					long acc_id = getRandomArrayVal(account_ids);
					long teller_id = getRandomArrayVal(teller_ids.toArray(new Long[0]));
					ConnectionManager.insertAccountWithdraw(t_id, acc_id, teller_id, conn);
				}

				if (account_owner_id != null) {
					// Finds an account that works with the person who did the transaction.
					List<Long> accounts = account_holder_ids.get(account_owner_id);
					if (accounts == null) {
						// If no account exists use cash instead.
						insert_cash_side = true;
					} else {
						// Insert account side transaciton.
						Long acc_id = getRandomArrayVal(accounts.toArray(new Long[0]));
						Long teller_id = getRandomArrayVal(teller_ids.toArray(new Long[0]));
						ConnectionManager.insertAccountWithdraw(t_id, acc_id, teller_id, conn);
					}
				}
				if (insert_cash_side) {
					// Insert what location cash was deposited at for the transaction.
					Long loc_id = getRandomArrayVal(location_ids);
					ConnectionManager.insertCashTransaction(t_id, loc_id, conn);
				}

			}
		} catch (Exception e) {
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
		return random.nextInt(array.length);
	}

	// Generates random timestamp between two specified years. 2020 goes to today.
	public static Timestamp generateRandomTimestampBetweenYears(int startYear, int endYear) {
		Timestamp startTimestamp = Timestamp.valueOf(startYear + "-01-01 00:00:00");

		Timestamp endTimestamp = Timestamp.valueOf(endYear + "-01-01 00:00:00");
		// 2020 means it is today. Can't happen in the future.
		if (endYear == 2020) {
			endTimestamp = new Timestamp(new Date().getTime());
		}
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
		return new Timestamp(offsetTime + (long) (random.nextDouble() * diff));
	}
}