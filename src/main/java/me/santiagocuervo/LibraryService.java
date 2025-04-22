package me.santiagocuervo;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryService {

    private static final Logger log = LoggerFactory.getLogger(LibraryService.class);
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private List<User> users;

    public LibraryService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        users = new ArrayList<>();
    }

    public void addBook(String id, String title, String author) {
        log.info("Entering LibraryService.addBook()");
        log.debug("Arguments: id {}, title {}, author {}", id, title, author);
        bookRepository.saveBook(new Book(id, title, author));
        log.info("Book saved");
    }

    public Book getBookById(String id) {
        log.info("Entering LibraryService.getBookById()");
        log.debug("Argument: id {}", id);

        log.debug("Verifying book existence...");
        if (bookRepository.findById(id) == null) {
            log.warn("Book with ID {} not found", id);
            throw new NoSuchElementException(String.format("Book with ID '%s' not found", id));
        }

        log.info("A book has been found");
        return bookRepository.findById(id);
    }

    public void addUser(String id, String name) {
        log.info("Entering LibraryService.addUser()");
        log.debug("Arguments: id {}, name {}", id, name);
        users.add(new User(id, name));
        log.info("User added");
    }

    public void borrowBook(String userId, String bookId) throws LoanException {
        log.info("Entering LibraryService.borrowBook()");
        log.debug("Arguments: userId {}, bookId {}", userId, bookId);

        // Verify book existence - NoSuchElementException
        log.debug("Verifying book existence...");
        Book loanBook = null;

        try {
            loanBook = getBookById(bookId);
        } catch (NoSuchElementException ex) {
            log.warn("Error loaning book: [{}]", ex.getMessage());
            throw new NoSuchElementException(String.format("Error loaning book: [%s]", ex.getMessage()));
        }
        log.info("Book exists");

        // Verify book availability - LoanException
        log.debug("Verifying book availability...");
        if (bookRepository.findById(bookId).isBorrowed()) {
            log.warn("Error loaning book: [Book with ID {} is already taken]", bookId);
            throw new LoanException(String.format("Error loaning book: [Book with ID '%s' is already taken]", bookId));
        }
        log.info("Book is available for loan");

        // Verify user existence - IllegalArgumentException
        log.debug("Verifying user existence...");
        User loanUser = null;

        for (User user : users) {
            if (user.getId().equalsIgnoreCase(userId.trim())) {
                loanUser = user;
            }
        }

        if (loanUser == null) {
            log.warn("Error loaning book: [User with ID {} not found]", userId);
            throw new IllegalArgumentException(
                    String.format("Error loaning book: [User with ID '%s' not found]", userId));
        }
        log.info("User was found");

        loanRepository.save(new Loan(loanUser, loanBook));
        log.info("Book loaned successfully");

    }

    public List<Loan> getLoansByUserId(String userId) throws LoanException {
        log.info("Entering LibraryService.getLoansByUserId()");
        log.debug("Argument: userId {}", userId);

        // Return list
        List<Loan> loanList = new ArrayList<>();

        // Finish execution if there are no loans yet
        if (loanRepository.getLoans().isEmpty()) {
            log.warn("No loans have been added yet", userId);
            throw new LoanException("No loans have been added yet");
        }

        // Verify user existence
        log.debug("Verifying user existence...");
        User loanUser = null;

        for (User user : users) {
            if (user.getId().equalsIgnoreCase(userId.trim())) {
                loanUser = user;
            }
        }

        if (loanUser == null) {
            log.warn("User with ID {} not found", userId);
            throw new IllegalArgumentException(String.format("User with ID '%s' not found", userId));
        }

        // Loop through loan list
        log.debug("Verifying user loans...");
        for (Loan loan : loanRepository.getLoans()) {
            if (loan.getUser().getId().equals(userId)) {
                loanList.add(loan);
            }
        }

        if (loanList.isEmpty()) {
            log.warn("User with ID {} has no loans", userId);
            throw new NoSuchElementException(String.format("User with ID '%s' has no loans", userId));
        }

        log.info("Loan has been found");
        return loanList;

    }

    public boolean isBookLoanedToUser(String userId, String bookId) throws LoanException {
        log.info("Entering LibraryService.isBookLoanedToUser()");
        log.debug("Argument: userId {}, bookId {}", userId, bookId);

        // Check if book exists and if it's loaned
        Book loanBook = getBookById(bookId);

        log.debug("Verifying that book has been loaned...");
        if (!loanBook.isBorrowed()) {
            log.warn("Book with ID {} hasn't been loaned", bookId);
            throw new LoanException(String.format("Book with ID '%s' hasn't been loaned", bookId));
        }
        log.info("Book is currently loaned");

        // Check if book loan matches user
        log.debug("Verifying that book loan matches user...");
        for (Loan loan : loanRepository.getLoans()) {
            if (loan.getBook().equals(loanBook) && loan.getUser().getId().equalsIgnoreCase(userId)) {
                log.info("Book loan matches user");
                return true;
            }
        }

        log.info("Book loan doesn't match user");
        return false;

    }

    public List<User> getUsers() {
        log.info("Entering LibraryService.isBookLoanedToUser()");
        log.info("Retrieving users...");
        return users;
    }

}
