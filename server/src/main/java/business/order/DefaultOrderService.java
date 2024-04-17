package business.order;

import api.ApiException;
import business.BookstoreDbException;
import business.JdbcUtils;
import business.book.Book;
import business.book.BookDao;
import business.cart.ShoppingCart;
import business.cart.ShoppingCartItem;
import business.customer.Customer;
import business.customer.CustomerDao;
import business.customer.CustomerForm;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultOrderService implements OrderService {

	private BookDao bookDao;
	private OrderDao orderDao;
	private LineItemDao lineItemDao;
	private CustomerDao customerDao;
	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}
	public void setOrderDao(OrderDao orderDao) {this.orderDao = orderDao;}
	public void setLineItemDao(LineItemDao lineItemDao) {this.lineItemDao = lineItemDao;}
	public void setCustomerDao(CustomerDao customerDao) {this.customerDao = customerDao;}
	@Override
	public OrderDetails getOrderDetails(long orderId) {
		Order order = orderDao.findByOrderId(orderId);
		Customer customer = customerDao.findByCustomerId(order.customerId());
		List<LineItem> lineItems = lineItemDao.findByOrderId(orderId);
		List<Book> books = lineItems
				.stream()
				.map(lineItem -> bookDao.findByBookId(lineItem.bookId()))
				.toList();
		return new OrderDetails(order, customer, lineItems, books);
	}

	private Date getCardExpirationDate(String monthString, String yearString) {
		int expiryYear = Integer.parseInt(yearString);
		int expiryMonth = Integer.parseInt(monthString);

		// Create a Calendar instance and set the year and month
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, expiryYear);
		// Calendar months are zero-based, so subtract 1 from the month
		calendar.set(Calendar.MONTH, expiryMonth - 1);

		// Set the day to 1 (or any other day you prefer)
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		// Get the java.util.Date object
		Date expirationDate = calendar.getTime();
		return expirationDate;
	}
	@Override
    public long placeOrder(CustomerForm customerForm, ShoppingCart cart) {

		validateCustomer(customerForm);
		validateCart(cart);
		try (Connection connection = JdbcUtils.getConnection()) {
			Date ccExpDate = getCardExpirationDate(
					customerForm.getCcExpiryMonth(),
					customerForm.getCcExpiryYear());
			return performPlaceOrderTransaction(
					customerForm.getName(),
					customerForm.getAddress(),
					customerForm.getPhone(),
					customerForm.getEmail(),
					customerForm.getCcNumber(),
					ccExpDate, cart, connection);
		} catch (SQLException e) {
			throw new BookstoreDbException("Error during close connection for customer order", e);
		}

	}
	private long performPlaceOrderTransaction(
			String name, String address, String phone,
			String email, String ccNumber, Date date,
			ShoppingCart cart, Connection connection) {
		try {
			connection.setAutoCommit(false);
			long customerId = customerDao.create(
					connection, name, address, phone, email,
					ccNumber, date);
			long customerOrderId = orderDao.create(
					connection,
					cart.getComputedSubtotal() + cart.getSurcharge(),
					generateConfirmationNumber(), customerId);
			for (ShoppingCartItem item : cart.getItems()) {
				lineItemDao.create(connection, customerOrderId,
						item.getBookId(), item.getQuantity());
			}
			connection.commit();
			return customerOrderId;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new BookstoreDbException("Failed to roll back transaction", e1);
			}
			return 0;
		}
	}
	private int generateConfirmationNumber(){
		return ThreadLocalRandom.current().nextInt(999999999);
	}


	private void validateCustomer(CustomerForm customerForm) {

    	String name = customerForm.getName();

		if (name == null || name.length() > 45 || name.length() < 4) {
			throw new ApiException.ValidationFailure("name", "Invalid name field");
		}

		// TODO: Validation checks for address, phone, email, ccNumber
		// Address must have at least 4 and at most 45 characters in length.
		String address = customerForm.getAddress();
		if (address == null || address.length() > 45 || address.length() < 4) {
			throw new ApiException.ValidationFailure("address", "Invalid address field");
		}
		// Phone: after removing all spaces, dashes, and patterns from the string it should have exactly 10 digits
		String phone = customerForm.getPhone();
		if (phone == null) {
			throw new ApiException.ValidationFailure("phone", "Missing phone field");
		}
		String phoneDigits = phone.replaceAll("\\D", "");
		if (phoneDigits.length() < 10) {
			throw new ApiException.ValidationFailure("phone", "Invalid phone field");
		}
		// Email: should not contain spaces; should contain a "@"; and the last character should not be "."
		String email = customerForm.getEmail();
		if (email == null || email.contains(" ") || !email.contains("@") || email.charAt(email.length()-1) == '.') {
			throw new ApiException.ValidationFailure("email", "Invalid email field");
		}
		// Credit card number: after removing spaces and dashes, the number of characters should be between 14 and 16
		String ccNumber = customerForm.getCcNumber();
		if (ccNumber == null) {
			throw new ApiException.ValidationFailure("ccNumber", "Missing ccNumber field");
		}
		String ccNumberDigits = ccNumber.replaceAll("\\D", "");
		if (ccNumberDigits.length() > 16 || ccNumberDigits.length() < 14) {
			throw new ApiException.ValidationFailure("ccNumber", "Invalid ccNumber field");
		}

		if (expiryDateIsInvalid(customerForm.getCcExpiryMonth(), customerForm.getCcExpiryYear())) {
			throw new ApiException.ValidationFailure("Please enter a valid expiration date.");
		}
	}

	private boolean expiryDateIsInvalid(String ccExpiryMonth, String ccExpiryYear) {

		// TODO: return true when the provided month/year is before the current month/yeaR
		// HINT: Use Integer.parseInt and the YearMonth class
		try {
			// Parse the expiry year and month
			int expiryYear = Integer.parseInt(ccExpiryYear);
			int expiryMonth = Integer.parseInt(ccExpiryMonth);

			// Create a YearMonth instance for the expiry date
			YearMonth expiryDate = YearMonth.of(expiryYear, expiryMonth);

			// Get the current year and month
			YearMonth currentDate = YearMonth.now();

			// Compare the expiry date with the current date
			return expiryDate.isBefore(currentDate);
		} catch (NumberFormatException | DateTimeException e) {
			// Return true if there is any parsing error, assuming the date is invalid
			return true;
		}

	}

	private void validateCart(ShoppingCart cart) {

		if (cart.getItems().size() <= 0) {
			throw new ApiException.ValidationFailure("Cart is empty.");
		}

		cart.getItems().forEach(item-> {
			if (item.getQuantity() < 0 || item.getQuantity() > 99) {
				throw new ApiException.ValidationFailure("Invalid quantity");
			}
			Book databaseBook = bookDao.findByBookId(item.getBookId());
			// TODO: complete the required validations
			if (databaseBook.price() != item.getBookForm().getPrice()) {
				throw new ApiException.ValidationFailure("Price Error");
			}
			if (databaseBook.categoryId() != item.getBookForm().getCategoryId()) {
				throw new ApiException.ValidationFailure("CategoryId Error");
			}
		});
	}

}
