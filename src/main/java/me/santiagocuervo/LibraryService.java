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

    // TODO: Test
    public void addUser(String id, String nombre) {
        users.add(new User(id, nombre));
    }

    public void borrowBook(String userId, String bookId) throws LoanException {

        // Verify book existence - NoSuchElementException
        Book loanBook = getBookById(bookId);

        // Verify book availability - LoanException
        if (bookRepository.findById(bookId).isBorrowed()) {
            throw new LoanException(String.format("Book with ID '%s' is already taken", bookId));
        }

        // Verify user existence - IllegalArgumentException
        User loanUser = null;

        for (User user : users) {
            if (user.getId().equalsIgnoreCase(userId.trim())) {
                loanUser = user;
            }
        }

        if (loanUser == null) {
            throw new IllegalArgumentException(String.format("User with ID '%s' not found", userId));
        }

        loanRepository.save(new Loan(loanUser, loanBook));

    }

    // FIXME: Implement
    public List<Loan> getLoansByUserId(String userId) {
        throw new Error("Not yet implemented");
    }

    // FIXME: Implement
    public boolean isBookLoanedToUser(String userId, String bookId) {
        throw new Error("Not yet implemented");
    }

    public List<User> getUsers() {
        return users;
    }

}
