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
        return bookRepository.findById(id);
    }

    public void addUser(String id, String nombre) {
        users.add(new User(id, nombre));
    }

    public void borrowBook(String userId, String bookId) {

        try {
            // Verify book existence - NoSuchElementException
            boolean doesBookExist = doesBookExist(bookId);

            // Verify book availability - LoanException
            verifyBookAvailability(bookId);

            // Verify user existence - IllegalArgumentException
            User loanUser = getUserById(userId);

            Book loanBook = getBookById(bookId);

            loanRepository.save(new Loan(loanUser, loanBook));
        } catch (NoSuchElementException | IllegalArgumentException | LoanException ex) {

            System.err.println(String.format("Error loaning book: [%s]", ex.getMessage()));

        }
    }

    public List<Loan> getLoansByUserId(String userId) {
        throw new Error("Not yet implemented");
    }

    public boolean isBookLoanedToUser(String userId, String bookId) {
        throw new Error("Not yet implemented");
    }

    public boolean doesBookExist(String bookId) {
        if (bookRepository.findById(bookId) == null) {
            throw new NoSuchElementException(String.format("Book with ID 's' not found", bookId));
        }

        return true;
    }

    public void verifyBookAvailability(String bookId) throws LoanException { // Método auxiliar creado para evitar
                                                                             // lanzar excepción en el
        // método
        // principal

        if (bookRepository.findById(bookId).isBorrowed() == false) {
            throw new LoanException(String.format("Book with ID '%s' is already taken", bookId));
        }

    }

    public User getUserById(String userId) {
        User loanUser = null;

        for (User user : users) {
            if (user.getId().equalsIgnoreCase(userId.trim())) {
                loanUser = user;
            }
        }

        if (loanUser == null) {
            throw new IllegalArgumentException(String.format("User with ID '%s' not found", userId));
        }

        return loanUser;
    }

}
