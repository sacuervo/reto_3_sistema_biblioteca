package me.santiagocuervo;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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

}
