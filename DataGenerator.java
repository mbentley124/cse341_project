import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DataGenerator {

	private static Random random = new Random(20);

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

	public static void insertTransaction(String id, double amount, Timestamp t_date, Connection conn) {
		try (PreparedStatement insert_transaction = conn.prepareStatement("INSERT INTO transaction VALUES (?, ?, ?)")) {
			insert_transaction.setString(1, id);
			insert_transaction.setDouble(2, amount);
			insert_transaction.setTimestamp(3, t_date);
			insert_transaction.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertCardPurchase(String t_id, String card_id, String v_id, Connection conn) {
		try (PreparedStatement insert = conn.prepareStatement("INSERT INTO card_purchase VALUES (?, ?, ?)")) {
			insert.setString(1, t_id);
			insert.setString(2, card_id);
			insert.setString(3, v_id);
			insert.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertLoanPayment(String t_id, String l_id, Connection conn) {
		try (PreparedStatement insert = conn.prepareStatement("INSERT INTO loan_payment VALUES (?, ?)")) {
			insert.setString(1, t_id);
			insert.setString(2, l_id);
			insert.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertCreditCardPayment(String t_id, String cc_id, Connection conn) {
		try (PreparedStatement insert = conn.prepareStatement("INSERT INTO credit_card_payment VALUES (?, ?)")) {
			insert.setString(1, t_id);
			insert.setString(2, cc_id);
			insert.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertAccountDeposit(String t_id, String acc_id, String teller_id, Connection conn) {
		try (PreparedStatement insert = conn.prepareStatement("INSERT INTO account_deposit VALUES (?, ?, ?)")) {
			insert.setString(1, t_id);
			insert.setString(2, acc_id);
			insert.setString(3, teller_id);
			insert.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertAccountWithdraw(String t_id, String acc_id, String loc_id, Connection conn) {
		try (PreparedStatement insert = conn.prepareStatement("INSERT INTO account_withdraw VALUES (?, ?, ?)")) {
			insert.setString(1, t_id);
			insert.setString(2, acc_id);
			insert.setString(3, loc_id);
			insert.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertCashTransaction(String t_id, String loc_id, Connection conn) {
		try (PreparedStatement insert = conn.prepareStatement("INSERT INTO cash_transaction VALUES (?, ?)")) {
			insert.setString(1, t_id);
			insert.setString(2, loc_id);
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
			// Insert vendors
			for (int i = 0; i < vendorNames.length; i++) {
				insertVendor(String.valueOf(i), vendorNames[i], conn);
			}

			// Generate customers.
			for (int i = 0; i < customerNames.length; i++) {
				insertCustomer(String.valueOf(i), customerNames[i], generateRandomTimestampBetweenYears(2000, 2010), conn);
			}

			List<Integer> employee_ids = new ArrayList<>();

			// Insert locations and their tellers.
			for (int i = 0; i < locationNames.length; i++) {
				insertLocation(String.valueOf(i), locationNames[i], conn);
				if (i + 1 == locationNames.length) {
					for (int x = i - 1; x < employeeNames.length; x++) {
						int emp_id = x + customerNames.length - 1;
						employee_ids.add(emp_id);
						insertEmployee(String.valueOf(emp_id), employeeNames[x], String.valueOf(i), (random.nextDouble() + 1) * 10,
								false, conn);
					}
				} else if (i > 0) {
					int emp_id = i - 1 + (customerNames.length - 1);
					employee_ids.add(emp_id);
					insertEmployee(String.valueOf(emp_id), employeeNames[i - 1], String.valueOf(i),
							(random.nextDouble() + 1) * 10, i == 1, conn);
				}
			}

			// From loans idd to loan holder id.
			int[] loanholder_ids = new int[TOTAL_LOANS];

			// Insert TOTAL_LOANS loans
			for (int i = 0; i < TOTAL_LOANS; i++) {
				String collatoral = null;
				if (random.nextDouble() > 0.5) {
					collatoral = getRandomArrayVal(collatoralNames);
				}
				int loanholder_id_int = getRandomIndex(customerNames);
				loanholder_ids[i] = loanholder_id_int;
				String loanholder_id = String.valueOf(loanholder_id_int);
				double loan_interest = random.nextDouble() * 10;
				int amount_loaned = (int) (random.nextDouble() * 50000) + 5000;
				int amount_due = amount_loaned - (int) (random.nextDouble() * 5000);
				int monthly_payment = (int) (random.nextDouble() * 200) + 50;
				insertLoan(String.valueOf(i), loanholder_id, loan_interest, amount_loaned, amount_due, monthly_payment,
						collatoral, conn);
			}

			// From account holder id to their account ids.
			Map<String, List<String>> account_holder_ids = new HashMap<>();
			List<String> checking_account_ids = new ArrayList<>();

			// Insert TOTAL_ACCOUNTS accounts (savings and checking)
			for (int i = 0; i < TOTAL_ACCOUNTS; i++) {
				int balance = (int) (random.nextDouble() * 4000) + 400;
				double interest_rate = random.nextDouble() * 0.001;

				Integer minimum_balance = null;
				Integer penalty = null;
				if (random.nextDouble() > 0.5) {
					minimum_balance = (int) Math.round(random.nextDouble() * 300);
					penalty = (int) Math.round((random.nextDouble() * 25) + 5);
				} else {
					checking_account_ids.add(String.valueOf(i));
				}

				String[] accountHolders;

				if (random.nextDouble() > 0.7) {
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
				for (String account_holder : accountHolders) {
					List<String> current_account_ids = account_holder_ids.getOrDefault(account_holder, new ArrayList<>());
					current_account_ids.add(String.valueOf(i));
					account_holder_ids.put(account_holder, current_account_ids);
				}
				// complete_account_holders[i] = accountHolders;

				insertAccount(String.valueOf(i), balance, interest_rate, minimum_balance, penalty, accountHolders, conn);
			}

			Map<Integer, Integer> credit_card_holders = new HashMap<>();

			// Insert all the cards.
			for (int i = 0; i < cardNames.length; i++) {
				String id = String.valueOf(i);
				String card_name = cardNames[i];
				Timestamp opened_timestamp = generateRandomTimestampBetweenYears(2011, 2017);
				if (random.nextDouble() > 0.5) {
					// Credit card
					int cardholder_id_int = getRandomIndex(customerNames);
					credit_card_holders.put(i, cardholder_id_int);
					String cardholder_id = String.valueOf(cardholder_id_int);
					double credit_interest_rate = (random.nextDouble() * 12) + 1;
					int credit_limit = (int) (Math.floor(random.nextDouble() * 5) + 1) * 500;
					double balance_due = random.nextDouble() * 500;
					double rolling_balance = random.nextDouble() * 500 + balance_due;
					insertCreditCard(id, card_name, opened_timestamp, cardholder_id, credit_interest_rate, credit_limit,
							balance_due, rolling_balance, conn);
				} else {
					// Debit card
					String account_id = getRandomArrayVal(checking_account_ids.toArray()).toString();
					insertDebitCard(id, card_name, opened_timestamp, account_id, conn);
				}
			}

			// Inserts TOTAL_TRANSACTIONS amount of transactions
			for (int i = 0; i < TOTAL_TRANSACTIONS; i++) {
				double amount = random.nextDouble() * 500 + 1;
				// Transactions happen between 2019 and today.
				Timestamp transaction_timestamp = generateRandomTimestampBetweenYears(2019, 2020);

				String t_id = String.valueOf(i);
				insertTransaction(t_id, amount, transaction_timestamp, conn);

				// Values used on whether or not the transaction type has another side to it
				// (where the money comes from)
				Integer account_owner_id = null;
				boolean insert_cash_side = false;

				// Determines the transaction type.
				double rand = random.nextDouble();
				if (rand < 0.2) {
					// Loan payment
					int loan_id = random.nextInt(TOTAL_LOANS);
					insertLoanPayment(t_id, String.valueOf(loan_id), conn);
					if (random.nextDouble() < 0.5) {
						// From cash
						insert_cash_side = true;
					} else {
						// From account.
						account_owner_id = loanholder_ids[loan_id];
					}
				} else if (rand < 0.4) {
					// Credit card payment
					int card_id = getRandomArrayVal(credit_card_holders.keySet().toArray(new Integer[0]));
					insertCreditCardPayment(t_id, String.valueOf(card_id), conn);
					if (random.nextDouble() < 0.5) {
						insert_cash_side = true;
					} else {
						account_owner_id = credit_card_holders.get(card_id);
					}
				} else if (rand < 0.6) {
					// Credit card purchase. No other side to the transaction.
					String card_id = String.valueOf(getRandomIndex(cardNames));
					String vendor_id = String.valueOf(getRandomIndex(vendorNames));
					insertCardPurchase(t_id, card_id, vendor_id, conn);
				} else if (rand < 0.8) {
					// Account deposit.
					int acc_id = random.nextInt(TOTAL_ACCOUNTS);
					int emp_id = getRandomArrayVal(employee_ids.toArray(new Integer[0]));
					insertAccountDeposit(t_id, String.valueOf(acc_id), String.valueOf(emp_id), conn);
					// Always uses cash.
					insert_cash_side = true;
				} else {
					// Withdrawing money from an account. No need for other side of transaction
					// since it has to be cash.
					int acc_id = random.nextInt(TOTAL_ACCOUNTS);
					int loc_id = getRandomIndex(locationNames);
					insertAccountWithdraw(t_id, String.valueOf(acc_id), String.valueOf(loc_id), conn);
				}

				if (account_owner_id != null) {
					// Finds an account that works with the person who did the transaction.
					List<String> accounts = account_holder_ids.get(String.valueOf(account_owner_id));
					if (accounts == null) {
						// If no account exists use cash instead.
						insert_cash_side = true;
					} else {
						// Insert account side transaciton.
						String acc_id = getRandomArrayVal(accounts.toArray(new String[0]));
						String loc_id = String.valueOf(getRandomIndex(locationNames));
						insertAccountWithdraw(t_id, acc_id, loc_id, conn);
					}
				}
				if (insert_cash_side) {
					// Insert what location cash was deposited at for the transaction.
					String loc_id = String.valueOf(getRandomIndex(locationNames));
					insertCashTransaction(t_id, loc_id, conn);
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