package me.santiagocuervo;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.when;

import org.mockito.MockitoAnnotations;

public class LibraryServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private LoanRepository loanRepository;
    private LibraryService libraryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        libraryService = new LibraryService(bookRepository, loanRepository);
    }

    @Test
    void testAddBook() {

        String bookId = "001";
        String bookTitle = "Meditations";
        String bookAuthor = "Marcus Aurelius";

        libraryService.addBook(bookId, bookTitle, bookAuthor);

        Mockito.verify(bookRepository, Mockito.atLeastOnce()).saveBook(Mockito.any(Book.class));
        Mockito.verify(bookRepository, Mockito.atMostOnce()).saveBook(Mockito.any(Book.class));
    }

    @Test
    void testAddUser() {

        String userId = "1234";
        String userName = "Santiago";

        libraryService.addUser(userId, userName);

        // Assert list first element isn't null
        assertNotNull(libraryService.getUsers().get(0));

        User user = libraryService.getUsers().get(0);

        // Assert correct ID
        assertEquals(userId, user.getId());

        // Assert correct name
        assertEquals(userName, user.getNombre());

        // Assert correct user list length
        assertEquals(1, libraryService.getUsers().size());
    }

    @Test
    void testBorrowBookSuccessfully() throws LoanException {
        String userId = "1234";
        String bookId = "5678";

        libraryService.addUser(userId, "Santiago");
        Book stubBook = new Book(bookId, "Meditations", "Marcus Aurelius", false);

        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(stubBook);

        libraryService.borrowBook(userId, bookId);

        Mockito.verify(bookRepository, Mockito.atLeastOnce()).findById(bookId);
        Mockito.verify(loanRepository).save(Mockito.any(Loan.class));

    }

    @Test
    void testBorrowNonExistingBook() {
        String userId = "1234";
        String bookId = "5678";

        assertThrows(NoSuchElementException.class, () -> libraryService.borrowBook(userId, bookId));

        Mockito.verify(bookRepository).findById(bookId);
    }

    @Test
    void testBorrowUnavailableBook() {
        String userId = "001";
        String bookId = "001";

        Book stubBook = new Book(bookId, "Meditations", "Marcus Aurelius", true);

        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(stubBook);

        assertThrows(LoanException.class, () -> libraryService.borrowBook(userId, bookId));

        Mockito.verify(bookRepository, Mockito.atLeastOnce()).findById(bookId);
        assert stubBook.isBorrowed();
    }

    @Test
    void testBorrowToNonExistingUser() {
        String userId = "001";
        String bookId = "001";

        Book stubBook = new Book(bookId, "Meditations", "Marcus Aurelius");

        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(stubBook);

        assertThrows(IllegalArgumentException.class, () -> libraryService.borrowBook(userId, bookId));

        Mockito.verify(bookRepository, Mockito.atLeastOnce()).findById(bookId);
        assert !stubBook.isBorrowed();

    }

    @Test
    void testGetLoansByUserId_Successfully() throws LoanException {

        String userId = "001";
        String userName = "Santiago";
        String bookId = "001";
        String bookName = "Meditations";
        String bookAuthor = "Marcus Aurelius";

        // Create user in libraryService list
        libraryService.addUser(userId, userName);

        // Create stubs
        List<Loan> stubLoans = new ArrayList<>();
        User stubUser = new User(userId, userName);
        Book stubBook = new Book(bookId, bookName, bookAuthor);

        Loan stubLoan = new Loan(stubUser, stubBook);
        stubLoans.add(stubLoan);

        Mockito.when(loanRepository.getLoans()).thenReturn(stubLoans);

        List<Loan> userLoans = libraryService.getLoansByUserId(userId);

        // Assert not empty userLoans
        assertNotEquals(0, userLoans.size());

        // Assert not null first element
        assertNotNull(userLoans.get(0));

        // Assert loan book matches stub book
        assertEquals(stubBook, userLoans.get(0).getBook());

        // Assert loan user matches stub user
        assertEquals(stubUser, userLoans.get(0).getUser());

        // Assert userLoans length is 1
        assertEquals(1, userLoans.size());
    }

    @Test
    void testGetLoansByUserId_WhenNoLoansExist() {

        String userId = "001";

        Mockito.when(loanRepository.getLoans()).thenReturn(new ArrayList<Loan>());

        assertThrows(LoanException.class, () -> libraryService.getLoansByUserId(userId));

        Mockito.verify(loanRepository, atLeastOnce()).getLoans();
    }

    @Test
    void testGetLoansByUserId_ForUnexistingUserId() {

        String userId = "001";
        String userName = "Santiago";
        String bookId = "001";
        String bookName = "Meditations";
        String bookAuthor = "Marcus Aurelius";

        // Create user in libraryService list
        libraryService.addUser(userId, userName);

        // Create stubs
        List<Loan> stubLoans = new ArrayList<>();
        User stubUser = new User(userId, userName);
        Book stubBook = new Book(bookId, bookName, bookAuthor);

        Loan stubLoan = new Loan(stubUser, stubBook);
        stubLoans.add(stubLoan);

        String nonExistingUserId = "002";

        when(loanRepository.getLoans()).thenReturn(stubLoans);

        assertThrows(IllegalArgumentException.class, () -> libraryService.getLoansByUserId(nonExistingUserId));

        Mockito.verify(loanRepository, atLeastOnce()).getLoans();
    }

    @Test
    void testGetLoansByUserId_ForNonExistingLoansForExistingUserId() throws LoanException {

        String userId = "001";
        String userName = "Santiago";

        String bookId = "001";
        String bookName = "Meditations";
        String bookAuthor = "Marcus Aurelius";

        String userWithNoLoansId = "002";
        String userWithNoLoansName = "Andrés";

        // Create user in libraryService list
        libraryService.addUser(userId, userName);
        libraryService.addUser(userWithNoLoansId, userWithNoLoansName);

        // Create stubs
        List<Loan> stubLoans = new ArrayList<>();
        User stubUser = new User(userId, userName);
        Book stubBook = new Book(bookId, bookName, bookAuthor);

        Loan stubLoan = new Loan(stubUser, stubBook);
        stubLoans.add(stubLoan);

        when(loanRepository.getLoans()).thenReturn(stubLoans);

        assertThrows(NoSuchElementException.class, () -> libraryService.getLoansByUserId(userWithNoLoansId));

        Mockito.verify(loanRepository, atLeastOnce()).getLoans();
    }

    @Test
    void testIsBookLoanedToUserSuccessfully() throws LoanException {

        String userId = "001";
        String bookId = "001";

        Book stubBook = new Book(bookId, "Meditations", "Marcus Aurelius", true);
        User stubUser = new User(bookId, "Santiago");
        Loan stubLoan = new Loan(stubUser, stubBook);

        List<Loan> stubLoanList = new ArrayList<>();
        stubLoanList.add(stubLoan);

        when(loanRepository.getLoans()).thenReturn(stubLoanList);

        when(bookRepository.findById(bookId)).thenReturn(stubBook);

        assertEquals(true, libraryService.isBookLoanedToUser(userId, bookId));

        Mockito.verify(loanRepository, atLeastOnce()).getLoans();
    }

    // TODO: Implement
    @Disabled
    @Test
    void testIsBookLoanedToUser_NonExistingBook() {
    }

    // TODO: Implement
    @Disabled
    @Test
    void testIsBookLoanedToUser_NonLoanedBook() {
    }

    // TODO: Implement
    @Disabled
    @Test
    void testIsBookLoanedToUser_BookLoanedToOtherUser() {
    }
}
