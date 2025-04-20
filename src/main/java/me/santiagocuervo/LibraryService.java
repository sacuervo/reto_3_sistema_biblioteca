package me.santiagocuervo;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class LibraryService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private List<User> users;

    public LibraryService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        users = new ArrayList<>();
    }

    public void addBook(String id, String title, String author) {
        bookRepository.saveBook(new Book(id, title, author));
    }

    public Book getBookById(String id) {
        if (bookRepository.findById(id) == null) {
            throw new NoSuchElementException(String.format("Book with ID '%s' not found", id));
        }

        return bookRepository.findById(id);
    }

    public void addUser(String id, String nombre) {
        users.add(new User(id, nombre));
    }

    public void borrowBook(String userId, String bookId) throws LoanException {

        // Verify book existence - NoSuchElementException
        Book loanBook = null;

        try {
            loanBook = getBookById(bookId);
        } catch (NoSuchElementException ex) {
            throw new NoSuchElementException(String.format("Error loaning book: [%s]", ex.getMessage()));
        }

        // Verify book availability - LoanException
        if (bookRepository.findById(bookId).isBorrowed()) {
            throw new LoanException(String.format("Error loaning book: [Book with ID '%s' is already taken]", bookId));
        }

        // Verify user existence - IllegalArgumentException
        User loanUser = null;

        for (User user : users) {
            if (user.getId().equalsIgnoreCase(userId.trim())) {
                loanUser = user;
            }
        }

        if (loanUser == null) {
            throw new IllegalArgumentException(
                    String.format("Error loaning book: [User with ID '%s' not found]", userId));
        }

        loanRepository.save(new Loan(loanUser, loanBook));

    }

    // TODO: Test
    public List<Loan> getLoansByUserId(String userId) throws LoanException {

        // Finish execution if there are no loans yet
        if (loanRepository.getLoans().isEmpty()) {
            throw new LoanException("No loans have been added yet");
        }

        // Return list
        List<Loan> loanList = new ArrayList<>();

        // Verify user existence
        User loanUser = null;

        for (User user : users) {
            if (user.getId().equalsIgnoreCase(userId.trim())) {
                loanUser = user;
            }
        }

        if (loanUser == null) {
            throw new IllegalArgumentException(String.format("User with ID '%s' not found", userId));
        }

        // Loop through loan list
        for (Loan loan : loanRepository.getLoans()) {
            if (loan.getUser().getId().equals(userId)) {
                loanList.add(loan);
            }
        }

        if (loanList.isEmpty()) {
            throw new NoSuchElementException(String.format("User with ID '%s' has no loans", userId));
        }

        return loanList;

    }

    // TODO: Test
    public boolean isBookLoanedToUser(String userId, String bookId) throws LoanException {

        // Check if book exists and if it's loaned
        Book loanBook = getBookById(bookId);

        if (!loanBook.isBorrowed()) {
            throw new LoanException(String.format("Book with ID '%s' hasn't been loaned", bookId));
        }

        // Check if book loan matches user

        for (Loan loan : loanRepository.getLoans()) {
            if (loan.getBook().equals(loanBook)) {
                return true;
            }
        }

        return false;

    }

    public List<User> getUsers() {
        return users;
    }

}
